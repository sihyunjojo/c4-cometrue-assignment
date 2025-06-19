package org.c4marathon.assignment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "pending_transfer")
public class PendingTransferJpaEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferStatus status;

	private LocalDateTime expiredAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_main_account_id", nullable = false)
	private MainAccountJpaEntity fromMainAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_main_account_id", nullable = false)
	private MainAccountJpaEntity toMainAccount;

	public PendingTransfer toDomain() {
		return PendingTransfer.to(
			id, 
			amount, 
			type, 
			status, 
			expiredAt, 
			fromMainAccount != null ? fromMainAccount.getId() : null, 
			toMainAccount != null ? toMainAccount.getId() : null
		);
	}
}
