package org.c4marathon.assignment.infra.properties;

import org.c4marathon.assignment.enums.SavingType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "business.rule.account.saving")
public class SavingAccountPolicy {

	@NotNull
	private Double fixedInterestRate = 0.05;

	@NotNull
	private Double flexibleInterestRate = 0.03;

	@NotNull
	private String accountPrefix = "02";

	public double getInterestRate(SavingType type) {
		return switch (type) {
			case FIXED -> fixedInterestRate;
			case FLEXIBLE -> flexibleInterestRate;
		};
	}
}
