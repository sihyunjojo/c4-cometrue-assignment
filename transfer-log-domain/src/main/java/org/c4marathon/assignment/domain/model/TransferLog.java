package org.c4marathon.assignment.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// import org.c4marathon.assignment.model.BaseTimeEntity;
import org.c4marathon.assignment.enums.TransferType;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.model.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "transfer_log", indexes = {
	@Index(name = "idx_from_number_send_time", columnList = "from_account_number, send_time, id"),
	@Index(name = "idx_to_number_receive_time", columnList = "to_account_number, receiver_time, id")
})
public class TransferLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true)
	private Long parentTransferTransactionId;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(name = "from_account_id")),
		@AttributeOverride(name = "type", column = @Column(name = "from_account_type")),
		@AttributeOverride(name = "number", column = @Column(name = "from_account_number"))
	})
	private AccountSnapshot from;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(name = "to_account_id")),
		@AttributeOverride(name = "type", column = @Column(name = "to_account_type")),
		@AttributeOverride(name = "number", column = @Column(name = "to_account_number"))
	})
	private AccountSnapshot to;

	@Column(nullable = false)
	private long amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransferStatus status;

	@Column(name = "send_time", nullable = true)
	private LocalDateTime sendTime;

	@Column(name = "receiver_time", nullable = true)
	private LocalDateTime receiverTime;
}
