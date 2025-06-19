package org.c4marathon.assignment.domain.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Settlement {

	private final Long targetMemberId; // 정산 대상자 (회사 or 참여자)
	private final Long amount;         // 정산 금액

	public Settlement(Long targetMemberId, Long amount) {
		if (targetMemberId == null || amount == null || amount < 0) {
			throw new IllegalArgumentException("정산 대상자 및 금액은 null이거나 음수일 수 없습니다.");
		}
		this.targetMemberId = targetMemberId;
		this.amount = amount;
	}

}
