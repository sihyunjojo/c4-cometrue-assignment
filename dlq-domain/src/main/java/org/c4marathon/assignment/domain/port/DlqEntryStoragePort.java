package org.c4marathon.assignment.domain.port;

import java.util.List;

import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.model.DlqStatus;
import org.c4marathon.assignment.pagination.PageRequest;

public interface DlqEntryStoragePort {

	List<DlqEntry> findByStatusOrderByFailedAtAsc(DlqStatus status, PageRequest pageable);
	DlqEntry save(DlqEntry dlqEntry);
}
