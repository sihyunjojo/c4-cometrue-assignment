package org.c4marathon.assignment.usecase.charge;

import org.c4marathon.assignment.domain.policy.MainAccountPolicy;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChargeUsecase {

	private final MainAccountPolicy mainAccountPolicy;
	private final MainAccountService mainAccountService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void charge(Long mainAccountId, Long shortfall, Long amount) {
		Long chargeAmount = mainAccountPolicy.getRoundedCharge(shortfall);
		mainAccountService.chargeOrThrow(mainAccountId, chargeAmount, amount);
	}
}
