package org.c4marathon.assignment.domain.model.vo;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.enums.AccountType;

public record MainAccountSnapshot(Long id, AccountType type, String number) {
	public static MainAccountSnapshot from(MainAccount account) {
		return new MainAccountSnapshot(account.getId(), account.getType(), account.getAccountNumber());
	}
}
