package org.c4marathon.assignment.domain.model;

import org.c4marathon.assignment.enums.AccountType;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountSnapshot {

	private Long id;

	@Enumerated(EnumType.STRING)
	private AccountType type;

	private String number;

	public static SavingAccountSnapshot from(SavingAccount account) {
		return new SavingAccountSnapshot(
			account.getId(),
			account.getType(),
			account.getAccountNumber()
		);
	}
}
