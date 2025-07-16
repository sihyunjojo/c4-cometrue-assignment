package org.c4marathon.assignment.domain.port;

import java.util.List;

import org.c4marathon.assignment.domain.model.DlqEntry;

public interface DlqEntryPublisherPort {

	/**
	 * 재처리를 위해 DLQ에서 처리 대기 중인 엔트리를 가져옵니다.
	 * @param limit 가져올 엔트리의 최대 개수
	 * @return 처리 대기 중인 DLQ 엔트리 목록
	 */
	List<DlqEntry> findPendingCreateTransferLogEntries(int limit);

	void offer(DlqEntry savedEntity);

	void removeFromDlqById(DlqEntry entry);

	int size();
}
