package org.c4marathon.assignment.model;

import org.c4marathon.assignment.enums.AccountType;

public interface Account {
	Long getId();

	Long getBalance();

	AccountType getType();

	String getAccountNumber();
}
