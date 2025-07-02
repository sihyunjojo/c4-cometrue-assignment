package org.c4marathon.assignment.infra.properties;

import org.c4marathon.assignment.domain.policy.MainAccountPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "business.rule.account.main")
public class InfraMainAccountPolicy implements MainAccountPolicy {

	// 💡 'business.rule.account.main-daily-limit' 값이 yml에 없을 경우 3000000으로 fallback (기본값)
	@NotNull
	private Long mainDailyLimit = 3_000_000L;

	@NotNull
	private Long chargeUnit = 10_000L;

	@NotNull
	private String accountPrefix = "01";

	public Long getRoundedCharge(Long shortfall) {
		return ((shortfall + chargeUnit - 1) / chargeUnit) * chargeUnit;
	}
}
