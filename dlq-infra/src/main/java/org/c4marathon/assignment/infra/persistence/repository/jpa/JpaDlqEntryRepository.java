package org.c4marathon.assignment.infra.persistence.repository.jpa;

import java.util.List;

import org.c4marathon.assignment.infra.persistence.entity.DlqEntryJpaEntity;
import org.c4marathon.assignment.domain.model.DlqStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDlqEntryRepository extends JpaRepository<DlqEntryJpaEntity, String> {
    List<DlqEntryJpaEntity> findByStatusOrderByFailedAtAsc(DlqStatus status, Pageable pageable);
}
