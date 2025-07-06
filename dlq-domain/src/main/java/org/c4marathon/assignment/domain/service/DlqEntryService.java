package org.c4marathon.assignment.domain.service;

import java.util.List;

import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.port.DlqEntryPublisherPort;
import org.c4marathon.assignment.domain.port.DlqEntryStoragePort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DlqEntryService {

	private final DlqEntryStoragePort dlqEntryStoragePort;
	private final DlqEntryPublisherPort dlqEntryPublisherPort;

	/**
	 * DLQ에 새로운 엔트리를 추가합니다.
	 *
	 * @param entry 추가할 DLQ 엔트리
	 */
	// @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션에서 실행
	public void save(DlqEntry entry) {
		DlqEntry savedEntity = dlqEntryStoragePort.save(entry);
		// DB에 저장 성공 후 인메모리 큐에도 추가
		dlqEntryPublisherPort.offer(savedEntity);
	}

	/**
	 * 재처리를 위해 DLQ에서 처리 대기 중인 엔트리를 가져옵니다.
	 * @param limit 가져올 엔트리의 최대 개수
	 * @return 처리 대기 중인 DLQ 엔트리 목록
	 */
	public List<DlqEntry> findPendingCreateTransferLogEntries(int limit) {
		return dlqEntryPublisherPort.findPendingCreateTransferLogEntries(limit);
	}

	/**
	 * DLQ 엔트리의 상태를 업데이트합니다.
	 *
	 * @param entry 업데이트할 DLQ 엔트리
	 */
	public void update(DlqEntry entry) {
		DlqEntry updatedEntity = dlqEntryStoragePort.save(entry); // save는 upsert 역할
		// 인메모리 큐에서도 업데이트 (기존 엔트리 제거 후 새 엔트리 추가)
		dlqEntryPublisherPort.replaceById(entry);
		dlqEntryPublisherPort.offer(updatedEntity);
	}

	/**
	 * 항목이 처리(저장소 업데이트)되었음을 표시하고, 동시에 관련 임시 데이터를 정리(인메모리 큐에서 제거)
	 *
	 * @param entry 업데이트할 DLQ 엔트리
	 */
	public void markAsProcessedAndCleanUp(DlqEntry entry) {
		dlqEntryStoragePort.save(entry);
		// 인메모리 큐에서도 업데이트 (기존 엔트리 제거)
		dlqEntryPublisherPort.replaceById(entry);
	}

	public int getMQSize() {
		return dlqEntryPublisherPort.size();
	}
}
