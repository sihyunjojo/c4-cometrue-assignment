package org.c4marathon.assignment.infra.persistence.jpa;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTransferLogRepository extends JpaRepository<TransferLog, Long> {
}
