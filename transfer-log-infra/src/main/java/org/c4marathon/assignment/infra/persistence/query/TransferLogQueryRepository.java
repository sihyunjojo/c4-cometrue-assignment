package org.c4marathon.assignment.infra.persistence.query;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TransferLogQueryRepository {
	Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(String accountNumber,
		LocalDateTime cursorTime, Long cursorId, int size);

	Slice<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(String accountNumber,
		LocalDateTime cursorTime, int size);

	Page<TransferLog> findPageByAccountNumber(String accountNumber, Pageable pageable);
}
