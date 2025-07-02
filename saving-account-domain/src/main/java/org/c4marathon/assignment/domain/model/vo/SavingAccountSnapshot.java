package org.c4marathon.assignment.domain.model.vo;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.enums.AccountType;

public record SavingAccountSnapshot(Long id, AccountType type, String number) {
	public static SavingAccountSnapshot from(SavingAccount account) {
		return new SavingAccountSnapshot(account.getId(), account.getType(), account.getAccountNumber());
	}
}
