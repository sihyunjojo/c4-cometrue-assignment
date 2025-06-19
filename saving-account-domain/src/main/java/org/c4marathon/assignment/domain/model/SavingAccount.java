package org.c4marathon.assignment.domain.model;

import org.c4marathon.assignment.enums.AccountType;
import org.c4marathon.assignment.enums.SavingType;
import org.c4marathon.assignment.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavingAccount implements Account {

	private final Long id;
	private final String accountNumber;
	private Long balance;
	private final Long subscribedDepositAmount;
	private final SavingType savingType;
	private final Long memberId;
	private final Long mainAccountId;
	private final Long version;


	public static SavingAccount createFixed(String accountNumber, Long memberId, Long mainAccountId, Long depositAmount) {
		if (depositAmount == null || depositAmount <= 0) throw new IllegalArgumentException("정기 적금 금액은 필수입니다.");
		return new SavingAccount(null, accountNumber, 0L, depositAmount, SavingType.FIXED, memberId, mainAccountId, null);
	}

	public static SavingAccount createFlexible(String accountNumber, Long memberId, Long mainAccountId) {
		return new SavingAccount(null, accountNumber, 0L, 0L, SavingType.FLEXIBLE, memberId, mainAccountId, null);
	}

	public Long calculateInterest(double rate) {
		return (long) (this.balance * rate);
	}

	public void deposit(Long amount) {
		this.balance += amount;
	}

	public AccountType getType() {
		return AccountType.SAVING_ACCOUNT;
	}
}
