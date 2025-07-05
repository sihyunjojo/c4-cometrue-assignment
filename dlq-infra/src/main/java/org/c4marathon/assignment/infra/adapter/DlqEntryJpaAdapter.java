package org.c4marathon.assignment.infra.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.c4marathon.assignment.domain.port.DlqEntryStoragePort;
import org.c4marathon.assignment.infra.persistence.entity.DlqEntryJpaEntity;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaDlqEntryRepository;
import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.model.DlqStatus;
import org.c4marathon.assignment.pagination.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DlqEntryJpaAdapter implements DlqEntryStoragePort {

    private final JpaDlqEntryRepository jpaDlqEntryRepository;

    @Override
    public List<DlqEntry> findByStatusOrderByFailedAtAsc(DlqStatus status, PageRequest pageable) {
		Pageable springPageable =
			org.springframework.data.domain.PageRequest.of(
				pageable.getPage(),
				pageable.getSize()
			);

        return jpaDlqEntryRepository.findByStatusOrderByFailedAtAsc(status, springPageable).stream()
                .map(DlqEntryJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public DlqEntry save(DlqEntry dlqEntry) {
        DlqEntryJpaEntity entity = new DlqEntryJpaEntity(dlqEntry);
        return jpaDlqEntryRepository.save(entity).toDomain();
    }
}
