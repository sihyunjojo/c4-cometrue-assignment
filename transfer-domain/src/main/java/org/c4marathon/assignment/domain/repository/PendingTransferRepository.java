package org.c4marathon.assignment.domain.repository;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PendingTransferRepository  {
    void save(PendingTransfer entity);
    
    Optional<PendingTransfer> findPendingPendingTransferById(Long transactionId);
    
    List<PendingTransfer> findRemindTargetsWithMainAccount(LocalDateTime time);
    
    List<PendingTransfer> findRemindTargetsWithMember(LocalDateTime time);
    
    Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime remindTime);
}
