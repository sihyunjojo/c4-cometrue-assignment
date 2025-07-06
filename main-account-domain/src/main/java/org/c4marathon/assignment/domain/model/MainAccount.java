package org.c4marathon.assignment.domain.model;

import java.time.LocalDateTime;

import org.c4marathon.assignment.enums.AccountType;
import org.c4marathon.assignment.model.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MainAccount implements Account {

	private final Long id;
	private final String accountNumber;
	private final Long balance;
	private final Long dailyChargeAmount;
	private final Long memberId;
	private final Long version;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static MainAccount create(String accountNumber, Long memberId) {
		if (memberId == null) throw new IllegalArgumentException("회원 ID는 필수입니다.");
		return MainAccount.builder()
			.accountNumber(accountNumber)
			.memberId(memberId)
			.balance(0L)
			.dailyChargeAmount(0L)
			.build();
	}

	public static MainAccount of(Long id, String accountNumber, Long balance, Long dailyChargeAmount, Long memberId, Long version) {
		return MainAccount.builder()
			.id(id)
			.accountNumber(accountNumber)
			.balance(balance)
			.dailyChargeAmount(dailyChargeAmount)
			.memberId(memberId)
			.version(version)
			.build();
	}

	public static MainAccount of(Long id, String accountNumber, Long balance, Long dailyChargeAmount, Long memberId, Long version, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return MainAccount.builder()
			.id(id)
			.accountNumber(accountNumber)
			.balance(balance)
			.dailyChargeAmount(dailyChargeAmount)
			.memberId(memberId)
			.version(version)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}

	public AccountType getType() {
		return AccountType.MAIN_ACCOUNT;
	}
}
