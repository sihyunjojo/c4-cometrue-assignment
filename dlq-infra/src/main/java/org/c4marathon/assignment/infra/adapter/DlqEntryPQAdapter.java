package org.c4marathon.assignment.infra.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.model.DlqStatus;
import org.c4marathon.assignment.domain.port.DlqEntryPublisherPort;
import org.c4marathon.assignment.domain.port.DlqEntryStoragePort;
import org.c4marathon.assignment.pagination.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class DlqEntryPQAdapter implements DlqEntryPublisherPort {

	private final DlqEntryStoragePort dlqEntryStoragePort;

	private final PriorityBlockingQueue<DlqEntry> inMemoryDlq;

	public DlqEntryPQAdapter(DlqEntryStoragePort dlqEntryStoragePort) {
		this.dlqEntryStoragePort = dlqEntryStoragePort;
		// PriorityBlockingQueue는 DlqEntry의 비교 기준이 필요합니다.
		// 여기서는 nextRetryAt을 기준으로 우선순위를 정하도록 하겠습니다.
		this.inMemoryDlq = new PriorityBlockingQueue<>(
			101,
			(e1, e2) -> {
				if (e1.getNextRetryAt() == null && e2.getNextRetryAt() == null) return 0;
				if (e1.getNextRetryAt() == null) return 1; // null은 나중에 처리
				if (e2.getNextRetryAt() == null) return -1; // null은 나중에 처리
				return e1.getNextRetryAt().compareTo(e2.getNextRetryAt());
			}
		);

		// 애플리케이션 시작 시 DB에 있는 PENDING 상태의 DLQ 엔트리를 인메모리 큐에 로드
		loadPendingEntriesFromDbToMemory();
	}

	public List<DlqEntry> findPendingCreateTransferLogEntries(int limit) {
		LocalDateTime now = LocalDateTime.now();
		return inMemoryDlq.stream()
			.filter(e -> e.getStatus() == DlqStatus.PENDING && e.getOperationType().equals("CREATE_TRANSFER_LOG") && (e.getNextRetryAt() == null || e.getNextRetryAt().isBefore(now) || e.getNextRetryAt().isEqual(now)))
			.limit(limit)
			.collect(Collectors.toList());
	}

	@Override
	public void offer(DlqEntry savedEntity) {
		inMemoryDlq.offer(savedEntity);
	}

	@Override
	public void removeFromDlqById(DlqEntry entry) {
		// inMemoryDlq에 저장된 항목들 중에서 entry라는 객체와 동일한 ID를 가진 모든 항목을 제거하는 역할
		inMemoryDlq.removeIf(e -> e.getId().equals(entry.getId()));
	}

	@Override
	public int size() {
		return inMemoryDlq.size();
	}

	// 애플리케이션 시작 시 DB에 있는 PENDING 상태의 DLQ 엔트리를 인메모리 큐에 로드
	private void loadPendingEntriesFromDbToMemory() {
		List<DlqEntry> pendingEntities = dlqEntryStoragePort.findByStatusOrderByFailedAtAsc(DlqStatus.PENDING, PageRequest.of(0, 1000)); // 초기 로드 개수 제한
		pendingEntities.forEach(inMemoryDlq::offer);
	}
}
