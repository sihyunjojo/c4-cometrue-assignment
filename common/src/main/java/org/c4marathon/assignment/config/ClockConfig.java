package org.c4marathon.assignment.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

	@Bean
	public Clock systemDefaultClock() {
		return Clock.systemDefaultZone();
	}
}
