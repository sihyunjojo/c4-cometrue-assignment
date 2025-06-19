package org.c4marathon.assignment.infra.persistence.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PendingTransferQueryRepositoryImpl implements PendingTransferQueryRepository {

    private final EntityManager em;

    @Override
    public Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime remindTime) {
        String jpql = "SELECT t FROM PendingTransfer t " +
                     "WHERE t.status = 'PENDING' AND t.expiredAt <= :remindTime";
        
        TypedQuery<PendingTransfer> query = em.createQuery(jpql, PendingTransfer.class);
        query.setParameter("remindTime", remindTime);
        
        List<PendingTransfer> pendingTransfers = query.getResultList();

        return pendingTransfers.stream()
                .collect(Collectors.groupingBy(
                        pendingTransfer -> {
                            try {
                                // MainAccount의 Member 직접 조회
                                return pendingTransfer.getToMainAccount().getMember();
                            } catch (Exception e) {
                                // 오류 발생 시 기본값 반환
                                throw new RuntimeException("Member 직접 조회 시 오류 발생", e);
                            }
                        }
                ));
    }
}
