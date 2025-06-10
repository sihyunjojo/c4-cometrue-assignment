package org.c4marathon.assignment.domain.repository.transferlog;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.transferlog.TransferLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TransferLogQueryRepository {
	Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(String accountNumber,
		LocalDateTime cursorTime, Long cursorId, int size);

	Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor2(String accountNumber,
		LocalDateTime cursorTime, Long cursorId, int size);

	Slice<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(String accountNumber,
		LocalDateTime cursorTime, int size);

	Page<TransferLog> findPageByAccountNumber(String accountNumber, Pageable pageable);

}
