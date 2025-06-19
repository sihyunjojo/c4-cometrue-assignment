package org.c4marathon.assignment.api.savingaccount.dto;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.enums.SavingType;

import lombok.Builder;

@Builder
public record SavingAccountResponseDto (String accountNumber, Long balance, SavingType savingType) {
	public static SavingAccountResponseDto of(SavingAccount savingAccount) {
		return SavingAccountResponseDto.builder()
			.accountNumber(savingAccount.getAccountNumber())
			.balance(savingAccount.getBalance())
			.savingType(savingAccount.getSavingType())
			.build();
	}
}
