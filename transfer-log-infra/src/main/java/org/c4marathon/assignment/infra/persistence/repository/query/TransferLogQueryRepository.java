package org.c4marathon.assignment.infra.persistence.repository.query;

import java.time.LocalDateTime;

import org.c4marathon.assignment.infra.persistence.entity.TransferLogJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TransferLogQueryRepository {
    Slice<TransferLogJpaEntity> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
        String accountNumber, LocalDateTime cursorTime, Long cursorId, int size);

    Slice<TransferLogJpaEntity> findAllByAccountNumberAndSendTimeAfterCursor(
        String accountNumber, LocalDateTime cursorTime, int size);

    Page<TransferLogJpaEntity> findPageByAccountNumber(String accountNumber, Pageable pageable);
}
