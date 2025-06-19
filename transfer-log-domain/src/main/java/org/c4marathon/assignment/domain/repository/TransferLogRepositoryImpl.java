package org.c4marathon.assignment.domain.repository;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.infra.persistence.jpa.JpaTransferLogRepository;
import org.c4marathon.assignment.infra.persistence.query.TransferLogQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransferLogRepositoryImpl implements TransferLogRepository {

	private final JpaTransferLogRepository jpa;
	private final TransferLogQueryRepository query;

	@Override
	public void save(TransferLog transferLog) {
		jpa.save(transferLog);
	}

	@Override
	public Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
		String accountNumber, LocalDateTime cursorTime, Long cursorId, int size) {
		return query.findAllByAccountNumberAndSendTimeAndIdAfterCursor(accountNumber, cursorTime, cursorId, size);
	}

	@Override
	public Slice<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(
		String accountNumber, LocalDateTime cursorTime, int size) {
		return query.findAllByAccountNumberAndSendTimeAfterCursor(accountNumber, cursorTime, size);
	}

	@Override
	public Page<TransferLog> findPageByAccountNumber(String accountNumber, Pageable pageable) {
		return query.findPageByAccountNumber(accountNumber, pageable);
	}
}
