package org.c4marathon.assignment.domain.command;

import java.util.List;

import org.c4marathon.assignment.model.policy.SettlementPolicy;

public record SettlementCommand(
	SettlementPolicy policyType,
	Long totalAmount,
	List<Long> participantMemberIdList
) {}
