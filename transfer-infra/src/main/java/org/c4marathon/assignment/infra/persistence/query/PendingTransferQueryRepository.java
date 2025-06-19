package org.c4marathon.assignment.infra.persistence.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.springframework.data.repository.query.Param;

public interface PendingTransferQueryRepository {
    Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(@Param("remindTime") LocalDateTime remindTime);
}
