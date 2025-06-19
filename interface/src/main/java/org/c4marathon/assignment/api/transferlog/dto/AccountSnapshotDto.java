package org.c4marathon.assignment.api.transferlog.dto;

import org.c4marathon.assignment.domain.model.AccountSnapshot;

import lombok.Builder;

@Builder
public record AccountSnapshotDto(
	Long id,
	String type,
	String number
) {
	public static AccountSnapshotDto from(AccountSnapshot snapshot) {
		if (snapshot == null) return null;
		return new AccountSnapshotDto(
			snapshot.getId(),
			snapshot.getType().name(),
			snapshot.getNumber()
		);
	}
}
