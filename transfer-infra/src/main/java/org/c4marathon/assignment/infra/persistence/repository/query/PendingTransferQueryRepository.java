package org.c4marathon.assignment.infra.persistence.repository.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;

public interface PendingTransferQueryRepository {
    Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime remindTime);
}
