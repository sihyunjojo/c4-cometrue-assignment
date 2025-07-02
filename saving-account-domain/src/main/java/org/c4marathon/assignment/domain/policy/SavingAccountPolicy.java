package org.c4marathon.assignment.domain.policy;

import org.c4marathon.assignment.enums.SavingType;

public interface SavingAccountPolicy {
	String getAccountPrefix();
	double getInterestRate(SavingType type);
}
