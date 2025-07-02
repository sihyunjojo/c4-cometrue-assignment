package org.c4marathon.assignment.infra.persistence.repository.jpa;

import org.c4marathon.assignment.infra.persistence.entity.TransferLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTransferLogRepository extends JpaRepository<TransferLogJpaEntity, Long> {
}
