package org.c4marathon.assignment;

import org.c4marathon.assignment.usecase.savingaccount.SavingAccountUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingAccountScheduler {

	private final SavingAccountUseCase savingAccountUseCase;

	// 매일 오전 4시: 이자 지급
	@Scheduled(cron = "0 0 4 * * *")
	public void applyDailyInterest() {
		try {
			savingAccountUseCase.applyDailyInterest();
			log.info("적금 이자 지급 완료");
		} catch (Exception e) {
			log.error("적금 이자 지급 중 오류 발생", e);
		}
	}

	// 매일 오전 8시: 정기 적금 자동 입금
	@Scheduled(cron = "0 0 8 * * *")
	public void autoDepositFixedSavings() {
		try {
			savingAccountUseCase.processFixedSavingDeposits();
			log.info("정기 적금 자동 출금 완료");
		} catch (Exception e) {
			log.error("정기 적금 자동 출금 중 오류 발생", e);
		}
	}
}
