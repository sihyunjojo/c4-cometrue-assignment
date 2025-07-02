package org.c4marathon.assignment.infra.persistence.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.infra.persistence.entity.PendingTransferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPendingTransferRepository extends JpaRepository<PendingTransferJpaEntity, Long> {
    // JPA의 전체 흐름과 책임 체계 따름
    @Query("SELECT t FROM PendingTransferJpaEntity t WHERE t.id = :toAccountId AND t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING")
    Optional<PendingTransferJpaEntity> findPendingTransferById(@Param("toAccountId") Long toAccountId);

    @Query("SELECT t FROM PendingTransferJpaEntity t LEFT JOIN t.toMainAccount m WHERE t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING AND t.expiredAt <= :remindTime")
    List<PendingTransferJpaEntity> findRemindPendingTargetTransactionsWithMainAccount(@Param("remindTime") LocalDateTime remindTime);

    @Query("SELECT t FROM PendingTransferJpaEntity t LEFT JOIN t.toMainAccount m LEFT JOIN m.member WHERE t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING AND t.createdAt <= :remindTime")
    List<PendingTransferJpaEntity> findRemindPendingTargetTransactionsWithMember(@Param("remindTime") LocalDateTime remindTime);
}
