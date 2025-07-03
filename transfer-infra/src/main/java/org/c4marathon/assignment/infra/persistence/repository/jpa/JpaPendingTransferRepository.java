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
    @Query("SELECT t FROM PendingTransferJpaEntity t WHERE t.id = :transactionId AND t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING")
    Optional<PendingTransferJpaEntity> findPendingTransferById(@Param("transactionId") Long transactionId);

    @Query("SELECT t FROM PendingTransferJpaEntity t LEFT JOIN t.toMainAccount m WHERE t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING AND t.createdAt <= :notificationReadyCutoffTime")
    List<PendingTransferJpaEntity> findRemindPendingTargetTransactionsWithMainAccount(@Param("notificationReadyCutoffTime") LocalDateTime notificationReadyCutoffTime);

    @Query("SELECT t FROM PendingTransferJpaEntity t LEFT JOIN t.toMainAccount m LEFT JOIN m.member WHERE t.status = org.c4marathon.assignment.enums.TransferStatus.PENDING AND t.createdAt  <= :notificationReadyCutoffTime")
    List<PendingTransferJpaEntity> findRemindPendingTargetTransactionsWithMember(@Param("notificationReadyCutoffTime") LocalDateTime notificationReadyCutoffTime);
}
