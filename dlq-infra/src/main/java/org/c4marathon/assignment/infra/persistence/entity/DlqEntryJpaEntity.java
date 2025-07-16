package org.c4marathon.assignment.infra.persistence.entity;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.model.DlqStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dlq_entry")
@Getter
@Setter
@NoArgsConstructor
public class DlqEntryJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String operationType;

    @Lob // Large Object, 긴 문자열 저장을 위해 사용
    @Column(nullable = false)
    private String payload;

    @Lob
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    @Column(nullable = false)
    private int retryCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DlqStatus status;

    private LocalDateTime nextRetryAt; // New field

    public DlqEntryJpaEntity(DlqEntry dlqEntry) {
        this.id = dlqEntry.getId();
        this.operationType = dlqEntry.getOperationType();
        this.payload = dlqEntry.getPayload();
        this.failureReason = dlqEntry.getFailureReason();
        this.failedAt = dlqEntry.getFailedAt();
        this.retryCount = dlqEntry.getRetryCount();
        this.status = dlqEntry.getStatus();
        this.nextRetryAt = dlqEntry.getNextRetryAt();
    }

    public DlqEntry toDomain() {
        return new DlqEntry(id, operationType, payload, failureReason, failedAt, retryCount, status, nextRetryAt);
    }
}
