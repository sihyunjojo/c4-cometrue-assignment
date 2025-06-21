package org.c4marathon.assignment.domain.repository;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PendingTransferRepository  {
    void save(PendingTransfer entity);
    
    Optional<PendingTransfer> findPendingTransferById(Long transactionId);
    
    List<PendingTransfer> findRemindTargetsWithMainAccount(LocalDateTime notificationReadyCutoffTime);
    
    List<PendingTransfer> findRemindTargetsWithMember(LocalDateTime notificationReadyCutoffTime);
    
    Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime notificationReadyCutoffTime);
}
