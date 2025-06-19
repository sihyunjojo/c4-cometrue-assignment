package org.c4marathon.assignment.infra.properties;

import lombok.Getter;
import lombok.Setter;
import org.c4marathon.assignment.domain.policy.PendingTransferPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "transfer-policy")
public class InfraPendingTransferPolicy implements PendingTransferPolicy {

    private long pendingTransferExpireAfterDurationHours = 72;
    private long pendingTransferRemindDurationHours = 24;

    @Override
    public Duration getPendingTransferExpireAfterDurationHours() {
        return Duration.ofHours(pendingTransferExpireAfterDurationHours);
    }

    @Override
    public Duration getPendingTransferRemindDurationHours() {
        return Duration.ofHours(pendingTransferRemindDurationHours);
    }
}
