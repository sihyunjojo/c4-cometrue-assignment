package org.c4marathon.assignment.domain.policy;

import java.time.Duration;

public interface PendingTransferPolicy {

	Duration getPendingTransferExpireAfterDurationHours();
	Duration getPendingTransferRemindDurationHours();
}
