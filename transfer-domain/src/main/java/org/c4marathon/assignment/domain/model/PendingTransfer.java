package org.c4marathon.assignment.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PendingTransfer {

    private final Long id;
    private final Long amount;
    private final TransferType type;
    private TransferStatus status;
    private final LocalDateTime expiredAt;
    private final Long fromMainAccountId;
    private final Long toMainAccountId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PendingTransfer of(Long id, Long amount, TransferType type, TransferStatus status,
                                   LocalDateTime expiredAt, Long fromMainAccountId, Long toMainAccountId,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        return PendingTransfer.builder()
            .id(id)
            .amount(amount)
            .type(type)
            .status(status)
            .expiredAt(expiredAt)
            .fromMainAccountId(fromMainAccountId)
            .toMainAccountId(toMainAccountId)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

	public static PendingTransfer of(Long fromMainAccountId, Long toMainAccountId, Long amount, Duration expireAfter, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new PendingTransfer(
			null,
			amount,
			TransferType.PENDING,
			TransferStatus.PENDING,
			LocalDateTime.now().plus(expireAfter),
			fromMainAccountId,
			toMainAccountId,
			createdAt,
			updatedAt
		);
	}

	public static PendingTransfer of(Long fromMainAccountId, Long toMainAccountId, Long amount, Duration expireAfter) {
		return new PendingTransfer(
			null,
			amount,
			TransferType.PENDING,
			TransferStatus.PENDING,
			LocalDateTime.now().plus(expireAfter),
			fromMainAccountId,
			toMainAccountId,
			null,
			null
		);
	}

	public void markAsCompleted() {
		this.status = TransferStatus.COMPLETED;
	}

	public void markAsCanceled() {
		this.status = TransferStatus.CANCELED;
	}

	public void markAsExpired() {
		this.status = TransferStatus.EXPIRED;
	}

}
