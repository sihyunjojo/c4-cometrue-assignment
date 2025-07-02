package org.c4marathon.assignment.api.member.dto;

import org.c4marathon.assignment.domain.model.MainAccount;

import lombok.Builder;

@Builder
public record MainAccountResponseDto(String accountNumber, Long balance) {
	public static MainAccountResponseDto from(MainAccount mainAccount) {
		return MainAccountResponseDto.builder()
			.accountNumber(mainAccount.getAccountNumber())
			.balance(mainAccount.getBalance())
			.build();
	}
}
