package org.c4marathon.assignment.usecase.savingaccount;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.c4marathon.assignment.api.savingaccount.dto.SavingAccountResponseDto;
import org.c4marathon.assignment.api.savingaccount.dto.CreateFixedSavingAccountRequestDto;
import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.c4marathon.assignment.domain.service.SavingAccountService;
import org.c4marathon.assignment.domain.service.TransferLogFactory;
import org.c4marathon.assignment.domain.service.TransferLogService;
import org.c4marathon.assignment.domain.service.TransferService;
import org.c4marathon.assignment.infra.properties.MainAccountPolicy;
import org.c4marathon.assignment.model.Account;
import org.c4marathon.assignment.model.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.retry.RetryExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingAccountUseCase {

	private final MainAccountService mainAccountService;
	private final SavingAccountService savingAccountService;
	private final TransferService transferService;
	private final TransferLogService transferLogService;

	private final TransferLogFactory transferLogFactory;
	private final MainAccountPolicy mainAccountPolicy;
	private final RetryExecutor retryExecutor;

	@Transactional
	public SavingAccountResponseDto registerFixedSavingAccount(Long memberId, CreateFixedSavingAccountRequestDto request) {
		SavingAccount fixedSavingAccount = savingAccountService.createFixedSavingAccount(memberId, request.subscribedDepositAmount());
		return SavingAccountResponseDto.of(fixedSavingAccount);
	}

	@Transactional
	public SavingAccountResponseDto registerFlexibleSavingAccount(Long memberId) {
		SavingAccount flexibleSavingAccount = savingAccountService.createFlexibleSavingAccount(memberId);
		return SavingAccountResponseDto.of(flexibleSavingAccount);
	}

	@Transactional
	public void deposit(Long savingAccountId, Long amount) {
		Long mainAccountId = savingAccountService.getMainAccountId(savingAccountId);
		Long shortfall = mainAccountService.calculateShortfall(mainAccountId, amount);

		if (shortfall > 0) {
			long chargeAmount = mainAccountPolicy.getRoundedCharge(shortfall);
			mainAccountService.chargeOrThrow(mainAccountId, chargeAmount, amount);

			MainAccount toAccount = mainAccountService.getRefreshedAccount(mainAccountId);
			TransferLog chargeLog = transferLogFactory.createExternalChargeLog(
				ExternalAccountPolicy.TEMPORARY_CHARGING,
				toAccount,
				chargeAmount);
			transferLogService.saveTransferLog(chargeLog);
		}

		Callable<Void> performDeposit = () -> {
			var fromAccount = mainAccountService.getRefreshedAccount(mainAccountId);
			var toAccount = savingAccountService.getRefreshedAccount(savingAccountId);
			transferService.transferInNewTransaction(fromAccount, toAccount, amount);
			return null;
		};

		retryExecutor.executeWithRetry(performDeposit);

		MainAccount fromAccount = mainAccountService.getRefreshedAccount(mainAccountId);
		SavingAccount toAccount = savingAccountService.getRefreshedAccount(savingAccountId);
		TransferLog depositLog = transferLogFactory.createImmediateTransferLog(
			fromAccount, 
			toAccount, 
			amount);
		transferLogService.saveTransferLog(depositLog);
	}

	// fixme: ✅ "Service를 몫아서 호출하는 것" 자체는 문제가 아니다.
	// ✅ 하지만 UseCase에 비즈니스 로직이 복잡하게 들어가면 문제다.
	// ✅ 비즈니스 로직이 복잡해지면 Service로 넘기는 게 맞다.
	@Transactional
	public void processFixedSavingDeposits() {
		Map<MainAccount, List<SavingAccount>> amountMapping = savingAccountService.getSubscribedDepositAmount();

		// 각 메인 계좌별로 처리
		for (Map.Entry<MainAccount, List<SavingAccount>> entry : amountMapping.entrySet()) {
			MainAccount mainAccount = entry.getKey();

			List<SavingAccount> savingAccounts = entry.getValue();

			// 해당 메인 계좌에서 모든 적금 계좌로 입금해야 할 총 이자 금액 계산
			long totalAmount = getTotalAmount(savingAccounts);

			// 잔액 부족분 확인
			long shortfall = mainAccountService.calculateShortfall(mainAccount.getId(), totalAmount);

			// 잔액 부족 시 충전 진행
			if (shortfall > 0) {
				long chargeAmount = mainAccountPolicy.getRoundedCharge(shortfall);
				mainAccountService.chargeOrThrow(mainAccount.getId(), chargeAmount, totalAmount);

				TransferLog transferLog = transferLogFactory.createExternalChargeLog(
					ExternalAccountPolicy.TEMPORARY_CHARGING,
					mainAccount,
					chargeAmount);
				transferLogService.saveTransferLog(transferLog);
			}

			// 각 적금 계좌별로 이자 입금 처리
			for (SavingAccount savingAccount : savingAccounts) {
				transferToSavingAccount(savingAccount, mainAccount);
			}
		}
	}

	private void transferToSavingAccount(SavingAccount savingAccount, MainAccount mainAccount) {
		final Long fromAccountId = mainAccount.getId();
		final Long toAccountId = savingAccount.getId();
		final long amount = savingAccount.getSubscribedDepositAmount();

		try {
			Callable<Void> performTransfer = () -> {
				var from = mainAccountService.getRefreshedAccount(fromAccountId);
				var to = savingAccountService.getRefreshedAccount(toAccountId);
				transferService.transferInNewTransaction(from, to, amount);
				return null;
			};

			retryExecutor.executeWithRetry(performTransfer);
		} catch (Exception e) {
			// 에러 처리 로직 필요: 롤백 또는 보상 트랜잭션 등
			throw new RuntimeException("이자 입금 중 오류 발생: " + e.getMessage(), e);
		}

		Account fromAccount = mainAccountService.getRefreshedAccount(fromAccountId);
		Account toAccount = savingAccountService.getRefreshedAccount(toAccountId);
		TransferLog depositLog = transferLogFactory.createFixedTermTransferLog(
			fromAccount, 
			toAccount, 
			amount);
		transferLogService.saveTransferLog(depositLog);
	}

	private long getTotalAmount(List<SavingAccount> savingAccounts) {
		return savingAccounts.stream().mapToLong(SavingAccount::getSubscribedDepositAmount).sum();
	}

	@Transactional
	public void applyDailyInterest() {
		List<SavingAccount> accounts = savingAccountService.findAll();
		for (SavingAccount account : accounts) {
			Long interest = savingAccountService.applyInterest(account);
			TransferLog depositLog = transferLogFactory.createInterestPaymentLog(
				account,
				interest);
			transferLogService.saveTransferLog(depositLog);
		}
	}
}
