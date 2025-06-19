package org.c4marathon.assignment.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PendingTransfer extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder.Default
	private Long amount = 0L;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferStatus status;

	private LocalDateTime expiredAt;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_main_account_id", nullable = false)
	private MainAccount fromMainAccount;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_main_account_id", nullable = false)
	private MainAccount toMainAccount;

	public static PendingTransfer createPending(MainAccount fromMainAccount, MainAccount toMainAccount, Long amount,
		Duration expireAfter) {
		LocalDateTime expiredAt = LocalDateTime.now().plus(expireAfter);

		return new PendingTransfer(null, amount, TransferType.PENDING,
			TransferStatus.PENDING, expiredAt, fromMainAccount, toMainAccount);
	}

	public void markAsCompleted() {
		this.status = TransferStatus.COMPLETED;
	}

	public void markAsCanceled() {
		this.status = TransferStatus.CANCELED;
	}

	public void markAsExpired() {
		this.status = TransferStatus.EXPIRED;
	}
}
