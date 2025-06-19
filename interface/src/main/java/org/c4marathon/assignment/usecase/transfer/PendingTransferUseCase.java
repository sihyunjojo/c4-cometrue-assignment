package org.c4marathon.assignment.usecase.transfer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.c4marathon.assignment.domain.service.PendingTransferService;
import org.c4marathon.assignment.domain.service.ReminderService;
import org.c4marathon.assignment.domain.service.TransferLogFactory;
import org.c4marathon.assignment.domain.service.TransferLogService;
import org.c4marathon.assignment.usecase.charge.ChargeUsecase;
import org.c4marathon.assignment.model.Account;
import org.c4marathon.assignment.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.retry.RetryExecutor;
import org.c4marathon.assignment.api.transfer.dto.TransferPendingRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PendingTransferUseCase {

	private final MainAccountService mainAccountService;
	private final PendingTransferService pendingTransferService;
	private final TransferLogService transferLogService;
	private final TransferLogFactory transferLogFactory;
	private final ReminderService reminderService;

	private final RetryExecutor retryExecutor;
	private final ChargeUsecase chargeUsecase;

	@Transactional
	public void createPendingTransfer(TransferPendingRequestDto request) {
		Long shortfall = mainAccountService.calculateShortfall(request.fromAccountId(), request.amount());

		if (shortfall > 0) {
			chargeUsecase.charge(request.fromAccountId(), shortfall, request.amount());

			Account toAccount = mainAccountService.findById(request.fromAccountId());
			TransferLog transferLog = transferLogFactory.createExternalChargeLog(
				ExternalAccountPolicy.TEMPORARY_CHARGING,
				toAccount,
				shortfall);
			transferLogService.saveTransferLog(transferLog);
		}

		PendingTransfer tx = retryExecutor.executeWithRetry(
			() -> pendingTransferService.initiate(
				request.fromAccountId(),
				request.toAccountId(),
				request.amount()
			)
		);

		Account fromAccount = mainAccountService.findById(request.fromAccountId());
		Account toAccount2 = mainAccountService.findById(request.toAccountId());
		TransferLog immediateTransferLog = transferLogFactory.createPendingTransferLog(tx.getId(), fromAccount,
				toAccount2, request.amount(), tx.getCreatedAt());
		transferLogService.saveTransferLog(immediateTransferLog);
	}

	@Transactional
	public void acceptPendingTransfer(Long transactionId) {
		// 대기 중인 거래 조회
		PendingTransfer tx = pendingTransferService.findPendingPendingTransfer(transactionId);

		// 계좌 정보 조회
		MainAccount fromAccount = mainAccountService.findById(tx.getFromMainAccountId());
		MainAccount toAccount = mainAccountService.findById(tx.getToMainAccountId());

		// 거래 수락 실행
		retryExecutor.executeWithRetry(() -> pendingTransferService.accept(transactionId));

		// 거래 로그 생성 및 저장
		TransferLog transferLog = transferLogFactory.createCompletePendingTransferLog(
				tx.getId(),
				fromAccount,
				toAccount,
				tx.getAmount(),
				tx.getCreatedAt());
		transferLogService.saveTransferLog(transferLog);
	}

	@Transactional
	public void cancelPendingTransfer(Long transactionId) {
		// 대기 중인 거래 조회
		PendingTransfer tx = pendingTransferService.findPendingPendingTransfer(transactionId);

		// 계좌 정보 조회
		MainAccount fromAccount = mainAccountService.findById(tx.getFromMainAccountId());
		MainAccount toAccount = mainAccountService.findById(tx.getToMainAccountId());

		// 거래 취소 실행
		retryExecutor.executeWithRetry(() -> pendingTransferService.cancel(transactionId));

		// 거래 로그 생성 및 저장
		TransferLog transferLog = transferLogFactory.createCancelPendingTransferLog(
				tx.getId(),
				fromAccount,
				toAccount,
				tx.getAmount(),
				tx.getCreatedAt());
		transferLogService.saveTransferLog(transferLog);
	}

	// todo: 보상 로직 만들기
	@Transactional
	public void expirePendingTransfer() {
		List<PendingTransfer> expiredPendingPendingTransfers = pendingTransferService.findAllByExpiredPendingPendingTransferWithMainAccount();

		for (PendingTransfer tx : expiredPendingPendingTransfers) {
			pendingTransferService.expired(tx);

			// 계좌 정보 조회
			MainAccount fromAccount = mainAccountService.findById(tx.getFromMainAccountId());
			MainAccount toAccount = mainAccountService.findById(tx.getToMainAccountId());

			TransferLog transferLog = transferLogFactory.createExpirePendingTransferLog(
					tx.getId(),
					fromAccount,
					toAccount,
					tx.getAmount(),
					tx.getCreatedAt());
			transferLogService.saveTransferLog(transferLog);
		}
	}

	public void remindPendingTargetTransactionsByImproved() {
		Map<Member, List<PendingTransfer>> remindTargetGroupedByMember = pendingTransferService.findRemindTargetGroupedByMember();
		reminderService.remindTransactions(remindTargetGroupedByMember);
	}
}
