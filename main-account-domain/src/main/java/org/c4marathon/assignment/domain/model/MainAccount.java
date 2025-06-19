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
		return new MainAccount(null, accountNumber, 0L, 0L, memberId, null);
	}

	public static MainAccount to(Long id, String accountNumber, Long balance, Long dailyChargeAmount, Long memberId, Long version) {
		return new MainAccount(id, accountNumber, balance, dailyChargeAmount, memberId, version);
	}

	private MainAccount(Long id, String accountNumber, Long balance, Long dailyChargeAmount, Long memberId, Long version) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.dailyChargeAmount = dailyChargeAmount;
		this.memberId = memberId;
		this.version = version;
		createdAt = null;
		updatedAt = null;
	}

	public AccountType getType() {
		return AccountType.MAIN_ACCOUNT;
	}
}
