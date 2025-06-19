package org.c4marathon.assignment.domain.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.model.AccountSnapshot;
import org.c4marathon.assignment.model.Account;
import org.c4marathon.assignment.model.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.enums.TransferType;
import org.c4marathon.assignment.enums.TransferStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferLogFactory {

	private final Clock clock;

	// 메인 → 메인 즉시 송금
	public TransferLog createImmediateTransferLog(Account fromAccount, Account toAccount, long amount) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(null, from, to, amount, TransferType.IMMEDIATE, TransferStatus.COMPLETED);
	}

	// 메인 → 메인 대기 송금
	public TransferLog createPendingTransferLog(Long parentTransferTransactionId, Account fromAccount, Account toAccount, long amount, LocalDateTime senderTime) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(parentTransferTransactionId, from, to, amount, TransferType.PENDING, TransferStatus.PENDING, senderTime);
	}

	// 대기 송금 완료
	public TransferLog createCompletePendingTransferLog(Long parentTransferTransactionId, Account fromAccount, Account toAccount, long amount, LocalDateTime senderTime) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(parentTransferTransactionId, from, to, amount, TransferType.PENDING, TransferStatus.COMPLETED, senderTime);
	}

	// 대기 송금 취소
	public TransferLog createCancelPendingTransferLog(Long parentTransferTransactionId, Account fromAccount, Account toAccount, long amount, LocalDateTime senderTime) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(parentTransferTransactionId, from, to, amount, TransferType.PENDING, TransferStatus.CANCELED, senderTime);
	}

	// 대기 송금 만료
	public TransferLog createExpirePendingTransferLog(Long parentTransferTransactionId, Account fromAccount, Account toAccount, long amount, LocalDateTime senderTime) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(parentTransferTransactionId, from, to, amount, TransferType.PENDING, TransferStatus.EXPIRED, senderTime);
	}

	// 메인 → 적금 송금
	public TransferLog createFixedTermTransferLog(Account fromAccount, Account toAccount, long amount) {
		AccountSnapshot from = AccountSnapshot.from(fromAccount);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(null, from, to, amount, TransferType.FIXED_TERM, TransferStatus.COMPLETED);
	}

	public TransferLog createInterestPaymentLog(Account toAccount, long amount) {
		AccountSnapshot from = AccountSnapshot.from(ExternalAccountPolicy.COMPANY);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(null, from, to, amount, TransferType.INTEREST, TransferStatus.COMPLETED);
	}

	// 외부 → 메인 충전
	public TransferLog createExternalChargeLog(ExternalAccountPolicy externalAccount, Account toAccount, long amount) {
		AccountSnapshot from = AccountSnapshot.from(ExternalAccountPolicy.COMPANY);
		AccountSnapshot to = AccountSnapshot.from(toAccount);
		return buildTransferLog(null, from, to, amount, TransferType.CHARGE, TransferStatus.COMPLETED);
	}

	private TransferLog buildTransferLog(Long parentTransferTransactionId, AccountSnapshot from, AccountSnapshot to, long amount,
		TransferType transactionType, TransferStatus transactionStatus) {
		LocalDateTime now = LocalDateTime.now(clock);

		LocalDateTime senderTime = transactionType.isSenderTimeAuto(transactionStatus) ? now : null;
		LocalDateTime receiverTime = transactionType.isReceiverTimeAuto(transactionStatus) ? now : null;

		return TransferLog.builder()
			.parentTransferTransactionId(parentTransferTransactionId)
			.from(from)
			.to(to)
			.amount(amount)
			.type(transactionType)
			.status(transactionStatus)
			.sendTime(senderTime)
			.receiverTime(receiverTime)
			.build();
	}

	private TransferLog buildTransferLog(Long parentTransferTransactionId, AccountSnapshot from, AccountSnapshot to, long amount,
		TransferType transactionType, TransferStatus transactionStatus, LocalDateTime senderTime) {
		LocalDateTime now = LocalDateTime.now(clock);

		LocalDateTime receiverTime = transactionType.isReceiverTimeAuto(transactionStatus) ? now : null;

		return TransferLog.builder()
			.parentTransferTransactionId(parentTransferTransactionId)
			.from(from)
			.to(to)
			.amount(amount)
			.type(transactionType)
			.status(transactionStatus)
			.sendTime(senderTime)
			.receiverTime(receiverTime)
			.build();
	}
}
