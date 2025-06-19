package org.c4marathon.assignment;

import org.c4marathon.assignment.infra.properties.MainAccountPolicy;
import org.c4marathon.assignment.infra.properties.PendingTransferPolicyProperties;
import org.c4marathon.assignment.infra.properties.SavingAccountPolicy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = { MainAccountPolicy.class, SavingAccountPolicy.class, PendingTransferPolicyProperties.class})
public class AssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}

}
