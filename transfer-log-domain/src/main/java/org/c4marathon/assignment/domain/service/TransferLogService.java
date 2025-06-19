package org.c4marathon.assignment.domain.service;


import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.repository.TransferLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// 서비스는 항상 비즈니스 도메인 로직만 다룬다
@Service
@RequiredArgsConstructor
public class TransferLogService {

	private final TransferLogRepository transferLogRepository;

	@Transactional
	public void saveTransferLog(TransferLog transferLog) {
		transferLogRepository.save(transferLog);
	}

	public Slice<TransferLog> findAllBySendTimeAndIdAfterCursor(String accountNumber, LocalDateTime startAt, Long startId, int size) {
		return transferLogRepository.findAllByAccountNumberAndSendTimeAndIdAfterCursor(accountNumber, startAt, startId, size);
	}

	public Slice<TransferLog> findAllBySendTimeAfterCursor(String accountNumber, LocalDateTime startAt, int size) {
		return transferLogRepository.findAllByAccountNumberAndSendTimeAfterCursor(accountNumber, startAt, size);
	}

	public Page<TransferLog> findRecentLogs(String accountNumber, Pageable pageable) {
		return transferLogRepository.findPageByAccountNumber(accountNumber, pageable);
	}
}
