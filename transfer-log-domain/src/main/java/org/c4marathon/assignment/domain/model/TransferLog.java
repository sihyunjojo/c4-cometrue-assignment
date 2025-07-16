package org.c4marathon.assignment.domain.model;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.vo.AccountSnapshot;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.enums.TransferType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TransferLog {
    private Long id;
    private Long parentTransferTransactionId;
    private AccountSnapshot from;
    private AccountSnapshot to;
    private long amount;
    private TransferType type;
    private TransferStatus status;
    private LocalDateTime sendTime;
    private LocalDateTime receiverTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonCreator
    public static TransferLog create(
            @JsonProperty("id") Long id,
            @JsonProperty("parentTransferTransactionId") Long parentTransferTransactionId,
            @JsonProperty("from") AccountSnapshot from,
            @JsonProperty("to") AccountSnapshot to,
            @JsonProperty("amount") long amount,
            @JsonProperty("type") TransferType type,
            @JsonProperty("status") TransferStatus status,
            @JsonProperty("sendTime") LocalDateTime sendTime,
            @JsonProperty("receiverTime") LocalDateTime receiverTime,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt) {
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
