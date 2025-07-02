package org.c4marathon.assignment.domain.model;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.vo.AccountSnapshot;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TransferLog {
    private Long id;
    private Long parentTransferTransactionId;
    private final AccountSnapshot from;
    private final AccountSnapshot to;
    private final long amount;
    private final TransferType type;
    private final TransferStatus status;
    private final LocalDateTime sendTime;
    private final LocalDateTime receiverTime;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static TransferLog of(Long id, Long parentTransferTransactionId, AccountSnapshot from, AccountSnapshot to, 
                               long amount, TransferType type, TransferStatus status, LocalDateTime sendTime, 
                               LocalDateTime receiverTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return TransferLog.builder()
            .id(id)
            .parentTransferTransactionId(parentTransferTransactionId)
            .from(from)
            .to(to)
            .amount(amount)
            .type(type)
            .status(status)
            .sendTime(sendTime)
            .receiverTime(receiverTime)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
