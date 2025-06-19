package org.c4marathon.assignment.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import org.c4marathon.assignment.domain.command.SettlementCommand;
import org.c4marathon.assignment.domain.model.Settlement;
import org.c4marathon.assignment.model.policy.ExternalAccountPolicy;
import org.c4marathon.assignment.model.policy.SettlementPolicy;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

	public static final Long COMPANY_ID = ExternalAccountPolicy.COMPANY.getId();

	public List<Settlement> calculateSettlement(SettlementCommand command) {
		List<Long> participantMemberIdList = command.participantMemberIdList();
		long totalAmount = command.totalAmount();
		SettlementPolicy policyType = command.policyType();

		return switch (policyType) {
			case EQUAL -> calculateEqualShare(totalAmount, participantMemberIdList);
			case RANDOM -> calculateRandomShare(totalAmount, participantMemberIdList);
		};
	}

	// 1/n 후 나머지 값은 타 페이 서비스와 같이 회사에서 지원하는 방식
	private List<Settlement> calculateEqualShare(long total, List<Long> participantMemberIdList) {
		int count = participantMemberIdList.size();
		long base = total / count;
		long remainder = total % count;

		List<Settlement> responses = new ArrayList<>();
		for (Long participantId : participantMemberIdList) {
			responses.add(new Settlement(participantId, base));
		}

		// 남은 금액은 회사(-1)에서 부담
		if (remainder > 0) {
			responses.add(new Settlement(COMPANY_ID, remainder));
		}

		return responses;
	}

	private List<Settlement> calculateRandomShare(long total, List<Long> participantMemberIdList) {
		int count = participantMemberIdList.size();
		SplittableRandom rand = new SplittableRandom();
		List<Long> shares = new ArrayList<>();
		long remaining = total;

		for (int i = 0; i < count - 1; i++) {
			long max = remaining - (count - i - 1);
			long amount = 1 + rand.nextLong(max);
			shares.add(amount);
			remaining -= amount;
		}
		shares.add(remaining);

		List<Settlement> responses = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			responses.add(new Settlement(participantMemberIdList.get(i), shares.get(i)));
		}

		return responses;
	}
}
