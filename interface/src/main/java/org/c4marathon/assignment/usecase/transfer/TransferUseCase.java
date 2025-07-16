package org.c4marathon.assignment.usecase.transfer;


import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.c4marathon.assignment.domain.service.SavingAccountService;
import org.c4marathon.assignment.domain.service.TransferLogFactory;
import org.c4marathon.assignment.domain.service.TransferLogService;
import org.c4marathon.assignment.domain.service.TransferService;
import org.c4marathon.assignment.usecase.charge.ChargeUsecase;
import org.c4marathon.assignment.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.retry.RetryExecutor;
import org.c4marathon.assignment.api.transfer.dto.TransferRequestDto;
import org.c4marathon.assignment.api.transfer.dto.AccountNumberTransferRequestDto;
import org.c4marathon.assignment.usecase.dlq.DlqUsecase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계좌 이체 유스케이스를 처리하는 서비스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferUseCase {

	private final DlqUsecase dlqUsecase;
	private final MainAccountService mainAccountService;
	private final SavingAccountService savingAccountService;
	private final TransferService transferService;
	private final TransferLogService transferLogService;
	private final TransferLogFactory transferLogFactory;
	private final RetryExecutor retryExecutor;
	private final ChargeUsecase chargeUsecase;

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
			chargeUsecase.charge(accountId, shortfall, amount);

			MainAccount refreshedAccount = mainAccountService.findById(accountId);
			TransferLog transferLog = transferLogFactory.createExternalChargeLog(
				ExternalAccountPolicy.TEMPORARY_CHARGING, refreshedAccount, shortfall);
			transferLogService.saveTransferLog(transferLog);
		}
	}

	/**
	 * 일반 계좌 간(MainAccount -> MainAccount) 송금을 실행합니다.
	 */
	private void executeMainToMainTransfer(Long fromAccountId, Long toAccountId, Long amount) {
		MainAccount refreshedFromAccount = mainAccountService.findById(fromAccountId);
		MainAccount refreshedToAccount = mainAccountService.findById(toAccountId);

		// 송금 실행 (재시도 로직 포함)
		retryExecutor.executeWithRetry(() -> {
			transferService.transfer(refreshedFromAccount, refreshedToAccount, amount);
			log.debug("일반 계좌 간 송금 완료: {} -> {}, 금액: {}",
				refreshedFromAccount.getAccountNumber(),
				refreshedToAccount.getAccountNumber(),
				amount);
			return true;
		});

		// 송금 성공 시 로그 생성
		TransferLog transferLog = transferLogFactory.createImmediateTransferLog(
			refreshedFromAccount, refreshedToAccount, amount);

		saveTransferLogFailureToDlq(transferLog);
	}

	/**
	 * 일반 계좌에서 적금 계좌로(MainAccount -> SavingAccount) 송금을 실행합니다.
	 */
	private void executeMainToSavingTransfer(Long fromAccountId, Long toAccountId, Long amount) {
		MainAccount refreshedFromAccount = mainAccountService.findById(fromAccountId);
		SavingAccount refreshedToAccount = savingAccountService.findById(toAccountId);

		// 송금 실행 (재시도 로직 포함)
		retryExecutor.executeWithRetry(() -> {
			transferService.transfer(refreshedFromAccount, refreshedToAccount, amount);
			log.debug("일반 계좌에서 적금 계좌로 송금 완료: {} -> {}, 금액: {}",
				refreshedFromAccount.getAccountNumber(),
				refreshedToAccount.getAccountNumber(),
				amount);
			return true;
		});

		// 송금 성공 시 로그 생성
		TransferLog transferLog = transferLogFactory.createImmediateTransferLog(
			refreshedFromAccount, refreshedToAccount, amount);

		saveTransferLogFailureToDlq(transferLog);
	}

	private void saveTransferLogFailureToDlq(TransferLog transferLog) {
		try {
			transferLogService.saveTransferLog(transferLog);
		} catch (Exception e) {
			log.error("송금 이력 저장 실패, DLQ에 추가: {}", e.getMessage(), e);
			try {
				// TransferLog 객체를 JSON 문자열로 변환하여 payload에 저장
				dlqUsecase.saveDLQ(e, transferLog);
			} catch (Exception dlqException) {
				log.error("CRITICAL: DLQ 저장 실패. 데이터가 유실될 수 있습니다. 원인: {}", dlqException.getMessage(), dlqException);
				// DLQ 저장 실패는 심각한 문제이므로, 런타임 예외를 발생시켜 트랜잭션을 롤백하고 시스템에 알려야 합니다.
				throw new RuntimeException("DLQ 저장에 실패했습니다.", dlqException);
			}
		}
	}
}
