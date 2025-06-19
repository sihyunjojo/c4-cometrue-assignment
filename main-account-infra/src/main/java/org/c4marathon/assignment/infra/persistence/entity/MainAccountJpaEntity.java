package org.c4marathon.assignment.infra.persistence.entity;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.model.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "main_account", uniqueConstraints = {
	@UniqueConstraint(columnNames = "account_number"),
	@UniqueConstraint(columnNames = "member_id")
})
public class MainAccountJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", nullable = false, unique = true, length = 20)
	private String accountNumber;

	private Long balance;
	private Long dailyChargeAmount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private MemberJpaEntity member;

	@Version
	private Long version;

	public MainAccount toDomain() {
		return MainAccount.to(id, accountNumber, balance, dailyChargeAmount, member.getId(), version);
	}

	// public static MainAccountJpaEntity fromDomain(MainAccount domain) {
	// 	return MainAccountJpaEntity.builder()
	// 		.id(domain.getId())
	// 		.accountNumber(domain.getAccountNumber())
	// 		.balance(domain.getBalance())
	// 		.dailyChargeAmount(domain.getDailyChargeAmount())
	// 		.member(domain.getMemberId())
	// 		.version(domain.getVersion())
	// 		.build();
	// }
}
