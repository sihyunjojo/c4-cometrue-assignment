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
@Table(name = "saving_account", uniqueConstraints = {
	@UniqueConstraint(columnNames = "account_number")
})
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

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "main_account_id")
	private Long mainAccountId;

	@Version
	private Long version;

	public SavingAccount toDomain() {
		return new SavingAccount(id, accountNumber, balance, subscribedDepositAmount, savingType, memberId, mainAccountId, version);
	}

	public static SavingAccountJpaEntity fromDomain(SavingAccount domain) {
		return SavingAccountJpaEntity.builder()
			.id(domain.getId())
			.accountNumber(domain.getAccountNumber())
			.balance(domain.getBalance())
			.subscribedDepositAmount(domain.getSubscribedDepositAmount())
			.savingType(domain.getSavingType())
			.memberId(domain.getMemberId())
			.mainAccountId(domain.getMainAccountId())
			.version(domain.getVersion())
			.build();
	}
}
