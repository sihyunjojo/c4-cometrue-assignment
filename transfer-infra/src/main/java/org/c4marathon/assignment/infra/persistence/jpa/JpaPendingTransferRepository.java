package org.c4marathon.assignment.infra.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPendingTransferRepository extends JpaRepository<PendingTransfer, Long> {
	// JPA의 전체 흐름과 책임 체계 따름
	@Query("SELECT t FROM PendingTransfer t WHERE t.id = :toAccountId AND t.status = 'PENDING'")
	Optional<PendingTransfer> findPendingPendingTransferById(@Param("toAccountId") Long toAccountId);

	@Query("SELECT t FROM PendingTransfer t LEFT JOIN t.toMainAccount m WHERE t.status = 'PENDING' AND t.expiredAt <= :remindTime")
	List<PendingTransfer> findRemindPendingTargetTransactionsWithMainAccount(@Param("remindTime") LocalDateTime remindTime);

	@Query("SELECT t FROM PendingTransfer t LEFT JOIN t.toMainAccount m LEFT JOIN m.member WHERE t.status = 'PENDING' AND t.createdAt <= :remindTime")
	List<PendingTransfer> findRemindPendingTargetTransactionsWithMember(@Param("remindTime") LocalDateTime remindTime);
}
