package org.c4marathon.assignment.domain.service;


import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.repository.TransferLogRepository;
import org.c4marathon.assignment.pagination.PageRequest;
import org.c4marathon.assignment.pagination.PageResult;
import org.c4marathon.assignment.pagination.SliceResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// 서비스는 항상 비즈니스 도메인 로직만 다룬다
@Service
@RequiredArgsConstructor
public class TransferLogService {

	private final TransferLogRepository transferLogRepository;

	public void saveTransferLog(TransferLog transferLog) {
		transferLogRepository.save(transferLog);
	}

	public SliceResult<TransferLog> findAllBySendTimeAndIdAfterCursor(String accountNumber, LocalDateTime startAt, Long startId, int size) {
		return transferLogRepository.findAllByAccountNumberAndSendTimeAndIdAfterCursor(accountNumber, startAt, startId, size);
	}

	public SliceResult<TransferLog> findAllBySendTimeAfterCursor(String accountNumber, LocalDateTime startAt, int size) {
		return transferLogRepository.findAllByAccountNumberAndSendTimeAfterCursor(accountNumber, startAt, size);
	}

	public PageResult<TransferLog> findRecentLogs(String accountNumber, PageRequest request) {
		return transferLogRepository.findPageByAccountNumber(accountNumber, request);
	}
}
