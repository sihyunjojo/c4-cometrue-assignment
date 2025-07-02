package org.c4marathon.assignment.domain.repository;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.pagination.PageRequest;
import org.c4marathon.assignment.pagination.PageResult;
import org.c4marathon.assignment.pagination.SliceResult;

public interface TransferLogRepository {

	void save(TransferLog transferLog);

	SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(String accountNumber, LocalDateTime cursorTime, Long cursorId, int size);

	SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(
		String accountNumber, LocalDateTime cursorTime, int size);

	PageResult<TransferLog> findPageByAccountNumber(String accountNumber, PageRequest pageable);

}
