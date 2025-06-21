package org.c4marathon.assignment.infra.persistence.repository;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.c4marathon.assignment.domain.repository.PendingTransferRepository;
import org.c4marathon.assignment.infra.persistence.entity.MainAccountJpaEntity;
import org.c4marathon.assignment.infra.persistence.entity.PendingTransferJpaEntity;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMainAccountRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaPendingTransferRepository;
import org.c4marathon.assignment.infra.persistence.repository.query.PendingTransferQueryRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class PendingTransferRepositoryImpl implements PendingTransferRepository {

    private final JpaPendingTransferRepository jpaPendingTransferRepository;
    private final JpaMainAccountRepository jpaMainAccountRepository;
    private final PendingTransferQueryRepository query;

    @Override
    public void save(PendingTransfer entity) {
        MainAccountJpaEntity fromAccount = jpaMainAccountRepository.findById(entity.getFromMainAccountId())
            .orElseThrow(() -> new IllegalArgumentException("보내는 계좌를 찾을 수 없습니다: " + entity.getFromMainAccountId()));
            
        MainAccountJpaEntity toAccount = jpaMainAccountRepository.findById(entity.getToMainAccountId())
            .orElseThrow(() -> new IllegalArgumentException("받는 계좌를 찾을 수 없습니다: " + entity.getToMainAccountId()));
        
        PendingTransferJpaEntity jpaEntity = PendingTransferJpaEntity.fromDomain(entity, fromAccount, toAccount);
        jpaPendingTransferRepository.save(jpaEntity);
    }
    
    @Override
    public Optional<PendingTransfer> findPendingPendingTransferById(Long transactionId) {
        return jpaPendingTransferRepository.findPendingPendingTransferById(transactionId)
            .map(PendingTransferJpaEntity::toDomain);
    }

    @Override
    public List<PendingTransfer> findRemindTargetsWithMainAccount(LocalDateTime time) {
        return jpaPendingTransferRepository.findRemindPendingTargetTransactionsWithMainAccount(time).stream()
            .map(PendingTransferJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<PendingTransfer> findRemindTargetsWithMember(LocalDateTime time) {
        return jpaPendingTransferRepository.findRemindPendingTargetTransactionsWithMember(time).stream()
            .map(PendingTransferJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime remindTime) {
        return query.findRemindTargetGroupedByMember(remindTime);
    }
}
