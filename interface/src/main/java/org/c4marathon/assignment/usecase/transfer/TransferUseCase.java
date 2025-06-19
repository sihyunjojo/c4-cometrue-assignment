package org.c4marathon.assignment.usecase.transfer;


import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.c4marathon.assignment.domain.service.SavingAccountService;
import org.c4marathon.assignment.domain.service.TransferLogFactory;
import org.c4marathon.assignment.domain.service.TransferLogService;
import org.c4marathon.assignment.domain.service.TransferService;
import org.c4marathon.assignment.infra.properties.MainAccountPolicy;
import org.c4marathon.assignment.model.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.retry.RetryExecutor;
import org.c4marathon.assignment.api.transfer.dto.TransferRequestDto;
import org.c4marathon.assignment.api.transfer.dto.AccountNumberTransferRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계좌 이체 유스케이스를 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferUseCase {

	private final MainAccountService mainAccountService;
	private final SavingAccountService savingAccountService;
	private final TransferService transferService;
	private final TransferLogService transferLogService;
	private final TransferLogFactory transferLogFactory;
	private final RetryExecutor retryExecutor;
	private final MainAccountPolicy mainAccountPolicy;

	/**
	 * 계좌 ID를 기반으로 일반 계좌 간 이체를 수행합니다.
	 */
	@Transactional
	public void transfer(TransferRequestDto request) {
		final Long fromAccountId = request.fromAccountId();
		final Long toAccountId = request.toAccountId();
		final Long amount = request.amount();

		// 잔액 부족 시 충전 처리
		handleShortfall(fromAccountId, amount);

		// 송금 실행
		executeMainToMainTransfer(fromAccountId, toAccountId, amount);
	}

	/**
	 * 계좌 번호를 기반으로 일반 계좌 간 이체를 수행합니다.
	 */
	@Transactional
	public void transferByAccountNumber(AccountNumberTransferRequestDto request) {
		// 계좌번호로 계좌 조회 및 검증
		MainAccount fromAccount = mainAccountService.findByAccountNumberOrThrow(request.fromAccountNumber());
		MainAccount toAccount = mainAccountService.findByAccountNumberOrThrow(request.toAccountNumber());
		final Long amount = request.amount();

		// 잔액 부족 확인 및 필요시 충전
		handleShortfall(fromAccount.getId(), amount);

		// 송금 실행
		executeMainToMainTransfer(fromAccount.getId(), toAccount.getId(), amount);
	}

	/**
	 * 계좌 번호를 기반으로 일반 계좌에서 적금 계좌로 이체를 수행합니다.
	 */
	@Transactional
	public void transferFromMainToSavingByAccountNumber(AccountNumberTransferRequestDto request) {
		// 계좌번호로 계좌 조회 및 검증
		MainAccount fromAccount = mainAccountService.findByAccountNumberOrThrow(request.fromAccountNumber());
		SavingAccount toAccount = savingAccountService.findByAccountNumberOrThrow(request.toAccountNumber());
		final Long amount = request.amount();

		// 잔액 부족 확인 및 필요시 충전
		handleShortfall(fromAccount.getId(), amount);

		// 송금 실행
		executeMainToSavingTransfer(fromAccount.getId(), toAccount.getId(), amount);
	}

	/**
	 * 잔액 부족 시 충전을 처리하는 메서드
	 */
	private void handleShortfall(Long accountId, Long amount) {
		Long shortfall = mainAccountService.calculateShortfall(accountId, amount);

		if (shortfall > 0) {
			long chargeAmount = mainAccountPolicy.getRoundedCharge(shortfall);
			mainAccountService.chargeOrThrow(accountId, chargeAmount, amount);

			MainAccount refreshedAccount = mainAccountService.getRefreshedAccount(accountId);
			TransferLog transferLog = transferLogFactory.createExternalChargeLog(
				ExternalAccountPolicy.TEMPORARY_CHARGING, refreshedAccount, chargeAmount);
			transferLogService.saveTransferLog(transferLog);
		}
	}

	/**
	 * 일반 계좌 간(MainAccount -> MainAccount) 송금을 실행합니다.
	 */
	private void executeMainToMainTransfer(Long fromAccountId, Long toAccountId, Long amount) {
		MainAccount refreshedFromAccount = mainAccountService.getRefreshedAccount(fromAccountId);
		MainAccount refreshedToAccount = mainAccountService.getRefreshedAccount(toAccountId);

		// 송금 실행 (재시도 로직 포함)
		retryExecutor.executeWithRetry(() -> {
			transferService.transferInNewTransaction(refreshedFromAccount, refreshedToAccount, amount);
			log.debug("일반 계좌 간 송금 완료: {} -> {}, 금액: {}",
				refreshedFromAccount.getAccountNumber(),
				refreshedToAccount.getAccountNumber(),
				amount);
			return null;
		});

		// 송금 성공 시 로그 생성
		TransferLog transferLog = transferLogFactory.createImmediateTransferLog(
			refreshedFromAccount, refreshedToAccount, amount);
		transferLogService.saveTransferLog(transferLog);
	}

	/**
	 * 일반 계좌에서 적금 계좌로(MainAccount -> SavingAccount) 송금을 실행합니다.
	 */
	private void executeMainToSavingTransfer(Long fromAccountId, Long toAccountId, Long amount) {
		MainAccount refreshedFromAccount = mainAccountService.getRefreshedAccount(fromAccountId);
		SavingAccount refreshedToAccount = savingAccountService.getRefreshedAccount(toAccountId);

		// 송금 실행 (재시도 로직 포함)
		retryExecutor.executeWithRetry(() -> {
			transferService.transferInNewTransaction(refreshedFromAccount, refreshedToAccount, amount);
			log.debug("일반 계좌에서 적금 계좌로 송금 완료: {} -> {}, 금액: {}",
				refreshedFromAccount.getAccountNumber(),
				refreshedToAccount.getAccountNumber(),
				amount);
			return null;
		});

		// 송금 성공 시 로그 생성
		TransferLog transferLog = transferLogFactory.createImmediateTransferLog(
			refreshedFromAccount, refreshedToAccount, amount);
		transferLogService.saveTransferLog(transferLog);
	}
}
