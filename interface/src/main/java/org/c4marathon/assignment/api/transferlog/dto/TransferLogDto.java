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
		return TransferLogDto.builder()
			.id(log.getId())
			.parentTransactionId(log.getParentTransferTransactionId())
			.from(AccountSnapshotDto.from(log.getFrom()))
			.to(AccountSnapshotDto.from(log.getTo()))
			.amount(log.getAmount())
			.type(log.getType().name())
			.status(log.getStatus().name())
			.sendTime(log.getSendTime())
			.receiverTime(log.getReceiverTime())
			.createdAt(log.getCreatedAt())
			.updatedAt(log.getUpdatedAt())
			.build();
	}
}
