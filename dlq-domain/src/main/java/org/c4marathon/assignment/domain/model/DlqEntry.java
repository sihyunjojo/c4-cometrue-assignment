package org.c4marathon.assignment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JPA 엔티티 매핑을 위해 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 추가
public class DlqEntry {
    private String id; // DLQ 엔트리의 고유 식별자 (UUID)
    private String operationType; // 실패한 작업의 유형 (예: "CREATE_TRANSFER_LOG")
    private String payload;       // 실패한 작업을 재처리하는 데 필요한 데이터 (JSON 형태)
    private String failureReason; // 작업 실패의 원인 (예외 메시지 또는 스택 트레이스 요약)
    private LocalDateTime failedAt; // 작업이 처음 실패한 시간
    private int retryCount;       // 현재까지 재시도한 횟수
    private DlqStatus status;     // DLQ 엔트리의 현재 상태 (PENDING, PROCESSING, SUCCESS, FAILED)
    private LocalDateTime nextRetryAt; // 다음 재시도를 시도할 시간

    // Compact constructor for initial creation (delegates to AllArgsConstructor)
    public DlqEntry(String operationType, String payload, String failureReason) {
        this.id = UUID.randomUUID().toString();
        this.operationType = operationType;
        this.payload = payload;
        this.failureReason = failureReason;
        this.failedAt = LocalDateTime.now();
        this.retryCount = 0;
        this.status = DlqStatus.PENDING;
        this.nextRetryAt = LocalDateTime.now().plusMinutes(1); // Initial retry after 1 minute
    }

    // Methods for state changes - modify current instance and return this
    public void incrementRetryCount() {
        this.retryCount++;
	}

    public void markAsProcessing() {
        this.status = DlqStatus.PROCESSING;
	}

    public void markAsSuccess() {
        this.status = DlqStatus.SUCCESS;
	}

    public void markAsFailed() {
        this.status = DlqStatus.FAILED;
	}

    public void markAsPending() {
        this.status = DlqStatus.PENDING;
	}

    public void calculateNextRetryTime() {
        if (this.retryCount == 1) {
            this.nextRetryAt = this.failedAt.plusMinutes(1);
        } else if (this.retryCount == 2) {
            this.nextRetryAt = this.failedAt.plusMinutes(10);
        } else if (this.retryCount == 3) {
            this.nextRetryAt = this.failedAt.plusMinutes(30);
        } else {
            // Beyond 3 retries, mark as failed permanently
            this.status = DlqStatus.FAILED;
            this.nextRetryAt = null; // No more retries
        }
	}
}
