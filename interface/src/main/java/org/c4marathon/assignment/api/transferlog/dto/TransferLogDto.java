package org.c4marathon.assignment.api.transferlog.dto;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;

import lombok.Builder;

@Builder
public record TransferLogDto(
	Long id,
	Long parentTransactionId,
	AccountSnapshotDto from,
	AccountSnapshotDto to,
	long amount,
	String type,
	String status,
	LocalDateTime sendTime,
	LocalDateTime receiverTime,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static TransferLogDto from(TransferLog log) {
		return new TransferLogDto(
			log.getId(),
			log.getParentTransferTransactionId(),
			AccountSnapshotDto.from(log.getFrom()),
			AccountSnapshotDto.from(log.getTo()),
			log.getAmount(),
			log.getType().name(),
			log.getStatus().name(),
			log.getSendTime(),
			log.getReceiverTime(),
			log.getCreatedAt(),
			log.getUpdatedAt()
		);
	}
}
