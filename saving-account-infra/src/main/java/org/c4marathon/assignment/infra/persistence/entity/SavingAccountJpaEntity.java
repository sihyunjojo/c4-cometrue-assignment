package org.c4marathon.assignment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.enums.SavingType;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "saving_account", uniqueConstraints = {@UniqueConstraint(columnNames = "account_number")})
public class SavingAccountJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", nullable = false, unique = true, length = 20)
	private String accountNumber;

	private Long balance;
	private Long subscribedDepositAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SavingType savingType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private MemberJpaEntity member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_account_id")
	private MainAccountJpaEntity mainAccount;

	@Version
	private Long version;

	public SavingAccount toDomain() {
		return SavingAccount.of(id, accountNumber, balance, subscribedDepositAmount, savingType, member.getId(),
			mainAccount.getId(), version, getCreatedAt(), getUpdatedAt());
	}

	public static SavingAccountJpaEntity fromDomain(SavingAccount domain, MemberJpaEntity member,
		MainAccountJpaEntity mainAccount) {
		return SavingAccountJpaEntity.builder()
			.id(domain.getId())
			.accountNumber(domain.getAccountNumber())
			.balance(domain.getBalance())
			.subscribedDepositAmount(domain.getSubscribedDepositAmount())
			.savingType(domain.getSavingType())
			.member(member)
			.mainAccount(mainAccount)
			.version(domain.getVersion())
			.build();
	}
}
