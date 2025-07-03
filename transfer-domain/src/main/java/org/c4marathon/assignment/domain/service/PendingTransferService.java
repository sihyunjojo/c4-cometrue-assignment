package org.c4marathon.assignment.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.c4marathon.assignment.domain.policy.PendingTransferPolicy;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.c4marathon.assignment.domain.repository.PendingTransferRepository;
import org.springframework.stereotype.Service;


import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingTransferService {

	private final PendingTransferPolicy pendingTransferPolicy;
	private final PendingTransferRepository pendingTransferRepository;
	private final MainAccountRepository mainAccountRepository;

	public PendingTransfer initiate(Long fromAccountId, Long toAccountId, Long amount) {
		MainAccount fromAccount = mainAccountRepository.findById(fromAccountId)
			.orElseThrow(() -> new IllegalArgumentException("메인 계좌가 존재하지 않음"));
		int result = mainAccountRepository.withdrawByOptimistic(fromAccount.getId(), amount, fromAccount.getVersion());

		if (result == 0) {
			throw new OptimisticLockException("잔고 출금 실패 - 동시성 문제");
		}

		MainAccount toAccount = mainAccountRepository.findById(toAccountId)
			.orElseThrow(() -> new IllegalArgumentException("메인 계좌가 존재하지 않음"));

		PendingTransfer tx = PendingTransfer.of(fromAccountId, toAccountId, amount,
			pendingTransferPolicy.getPendingTransferExpireAfterDurationHours());

		pendingTransferRepository.save(tx);
		return tx;
	}

	public Boolean accept(Long transactionId) {
		PendingTransfer tx = pendingTransferRepository.findPendingTransferById(transactionId)
			.orElseThrow(() -> new IllegalArgumentException("대기 중인 거래가 존재하지 않음"));

		MainAccount toAccount = mainAccountRepository.findById(tx.getToMainAccountId())
			.orElseThrow(() -> new IllegalArgumentException("입금 계좌가 존재하지 않습니다"));

		int result = mainAccountRepository.depositByOptimistic(tx.getToMainAccountId(), tx.getAmount(),
			toAccount.getVersion());

		if (result == 0) {
			throw new OptimisticLockException("입금 실패 - 동시성 문제");
		}

		tx.markAsCompleted();
		pendingTransferRepository.save(tx);
		return true;
	}

	public Boolean cancel(Long transactionId) {
		PendingTransfer tx = pendingTransferRepository.findPendingTransferById(transactionId)
			.orElseThrow(() -> new IllegalArgumentException("대기 중인 거래가 존재하지 않음"));

		MainAccount fromAccount = mainAccountRepository.findById(tx.getFromMainAccountId())
			.orElseThrow(() -> new IllegalArgumentException("환불 계좌가 존재하지 않습니다"));

		int result = mainAccountRepository.depositByOptimistic(tx.getFromMainAccountId(), tx.getAmount(),
			fromAccount.getVersion());

		if (result == 0) {
			throw new OptimisticLockException("환불 실패 - 동시성 문제");
		}

		tx.markAsCanceled();
		pendingTransferRepository.save(tx);
		return true;
	}

	public void expired(PendingTransfer tx) {
		MainAccount fromAccount = mainAccountRepository.findById(tx.getFromMainAccountId())
			.orElseThrow(() -> new IllegalArgumentException("환불 계좌가 존재하지 않습니다"));

		int result = mainAccountRepository.depositByOptimistic(tx.getFromMainAccountId(), tx.getAmount(),
			fromAccount.getVersion());

		if (result == 0) {
			throw new OptimisticLockException("환불 실패 - 동시성 문제");
		}

		tx.markAsExpired();
		pendingTransferRepository.save(tx);
	}

	public PendingTransfer findPendingTransfer(Long transactionId) {
		return pendingTransferRepository.findPendingTransferById(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("대기 중인 거래가 존재하지 않음"));
	}

	public Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember() {
		Duration remindDurationHour = pendingTransferPolicy.getPendingTransferRemindDurationHours();
		LocalDateTime notificationReadyCutoffTime = LocalDateTime.now().minus(remindDurationHour);

		return pendingTransferRepository.findRemindTargetGroupedByMember(notificationReadyCutoffTime);
	}

	public List<PendingTransfer> findRemindPendingTargetTransactionsWithMember() {
		Duration remindDurationHour = pendingTransferPolicy.getPendingTransferRemindDurationHours();
		LocalDateTime notificationReadyCutoffTime = LocalDateTime.now().minus(remindDurationHour);

		return pendingTransferRepository.findRemindTargetsWithMember(notificationReadyCutoffTime);
	}

	public List<PendingTransfer> findRemindPendingTransferWithMainAccount() {
		Duration remindDurationHour = pendingTransferPolicy.getPendingTransferRemindDurationHours();
		LocalDateTime notificationReadyCutoffTime = LocalDateTime.now().minus(remindDurationHour);

		return pendingTransferRepository.findRemindTargetsWithMainAccount(notificationReadyCutoffTime);
	}
}
