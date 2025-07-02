package org.c4marathon.assignment.enums;

import lombok.Getter;

/**
 * 가독성 향상을 위한 enum 클래스
 */
@Getter
public enum AccountPolicy {
	MAIN_DAILY_LIMIT("메인계좌 일일 한도"),
	CHARGE_UNIT("충전 단위");

	private final String description;

	AccountPolicy(String description) {
		this.description = description;
	}
}
