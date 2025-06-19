package org.c4marathon.assignment.domain.model.vo;

import org.c4marathon.assignment.enums.AccountType;
import org.c4marathon.assignment.model.Account;
import org.c4marathon.assignment.policy.ExternalAccountPolicy;

public record AccountSnapshot(Long id, AccountType type, String number) {
	public static AccountSnapshot from(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Account must not be null");
		}
		return new AccountSnapshot(account.getId(), account.getType(), account.getAccountNumber());
	}

	public static AccountSnapshot from(ExternalAccountPolicy policy) {
		if (policy == null) {
			throw new IllegalArgumentException("ExternalAccountPolicy must not be null");
		}
		return new AccountSnapshot(policy.getId(), policy.getType(), policy.getNumber());
	}
}
