package org.c4marathon.assignment.domain.policy;

public interface MainAccountPolicy {
	String getAccountPrefix();
	Long getMainDailyLimit();
	Long getRoundedCharge(Long shortfall);
}
