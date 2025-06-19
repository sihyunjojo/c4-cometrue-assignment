package org.c4marathon.assignment.usecase.mainaccount;

import org.c4marathon.assignment.domain.service.MainAccountService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MainAccountUsecase {

	private final MainAccountService mainAccountService;

	public void resetAllDailyChargeAmount() {
		mainAccountService.resetAllDailyChargeAmount();
	}
}
