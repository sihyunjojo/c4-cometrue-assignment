package org.c4marathon.assignment.domain.model;

import org.c4marathon.assignment.enums.AccountType;
import org.c4marathon.assignment.enums.SavingType;
import org.c4marathon.assignment.model.Account;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saving_account", uniqueConstraints = {
	@UniqueConstraint(columnNames = "account_number"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SavingAccount implements Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", nullable = false, unique = true, length = 20)
	private String accountNumber;

	@Builder.Default
	private Long balance = 0L;

	@Builder.Default
	@Column(nullable = true)
	private Long subscribedDepositAmount = 0L;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SavingType savingType;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_account_id")
	private MainAccount mainAccount;

	@Version
	Long version;

	@Builder(access = AccessLevel.PRIVATE)
	private SavingAccount(String accountNumber, Long balance, Long subscribedDepositAmount,
		SavingType savingType, Member member, MainAccount mainAccount) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.subscribedDepositAmount = subscribedDepositAmount;
		this.savingType = savingType;
		this.member = member;
		this.mainAccount = mainAccount;
	}

	public static SavingAccount createFixed(String accountNumber, Member member, MainAccount mainAccount, Long depositAmount) {
		if (depositAmount == null || depositAmount <= 0) throw new IllegalArgumentException("정기 적금 금액은 필수입니다.");
		return SavingAccount.builder()
			.accountNumber(accountNumber)
			.balance(0L)
			.subscribedDepositAmount(depositAmount)
			.savingType(SavingType.FIXED)
			.member(member)
			.mainAccount(mainAccount)
			.build();
	}

	public static SavingAccount createFlexible(String accountNumber, Member member, MainAccount mainAccount) {
		return SavingAccount.builder()
			.accountNumber(accountNumber)
			.balance(0L)
			.savingType(SavingType.FLEXIBLE)
			.member(member)
			.mainAccount(mainAccount)
			.build();
	}

	public void deposit(Long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
		}
		this.balance += amount;
	}

	/**
	 * 매일 지급할 이자 계산 (단리 기준)
	 * 은행/저축은행/카카오뱅크, 토스 등 실제 약관들을 보면 절삭(버림) 방식이 일반적
	 */
	public Long calculateInterest(Double rate) {
		return (long) (this.balance * rate); // 소수점 절삭
	}

	public AccountType getType() {
		return AccountType.SAVING_ACCOUNT;
	}
}
