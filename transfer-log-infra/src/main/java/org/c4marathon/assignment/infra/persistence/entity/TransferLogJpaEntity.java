package org.c4marathon.assignment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;
import org.c4marathon.assignment.infra.persistence.embeddable.AccountSnapshotEmbeddable;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "transfer_log", indexes = {
	@Index(name = "idx_from_number_send_time", columnList = "from_account_number, send_time, id"),
	@Index(name = "idx_to_number_receive_time", columnList = "to_account_number, receiver_time, id")})
public class TransferLogJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long parentTransferTransactionId;

	@Embedded
	@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "from_account_id")),
		@AttributeOverride(name = "type", column = @Column(name = "from_account_type")),
		@AttributeOverride(name = "number", column = @Column(name = "from_account_number"))})
	private AccountSnapshotEmbeddable from;

	@Embedded
	@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "to_account_id")),
		@AttributeOverride(name = "type", column = @Column(name = "to_account_type")),
		@AttributeOverride(name = "number", column = @Column(name = "to_account_number"))})
	private AccountSnapshotEmbeddable to;

	private long amount;

	@Enumerated(EnumType.STRING)
	private TransferType type;

	@Enumerated(EnumType.STRING)
	private TransferStatus status;

	private LocalDateTime sendTime;

	private LocalDateTime receiverTime;

	// static 메서드로 변환 로직 제공
	public TransferLog toDomain() {
		return TransferLog.of(id, parentTransferTransactionId, from.toDomain(), to.toDomain(), amount, type, status,
			sendTime, receiverTime, getCreatedAt(), getUpdatedAt());
	}

	public static TransferLogJpaEntity fromDomain(TransferLog domain) {
		return TransferLogJpaEntity.builder()
			.id(domain.getId())
			.parentTransferTransactionId(domain.getParentTransferTransactionId())
			.from(AccountSnapshotEmbeddable.fromDomain(domain.getFrom())) // 도메인 → Embeddable 변환
			.to(AccountSnapshotEmbeddable.fromDomain(domain.getTo()))     // 도메인 → Embeddable 변환
			.amount(domain.getAmount())
			.type(domain.getType())
			.status(domain.getStatus())
			.sendTime(domain.getSendTime())
			.receiverTime(domain.getReceiverTime())
			.build();
	}
}
