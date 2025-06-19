package org.c4marathon.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.usecase.mainaccount.MainAccountUsecase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountScheduler {

	private final MainAccountUsecase mainAccountUseCase;

	// Todo: 배치 처리 또는 chunk 단위 업데이트, 혹은 비동기 처리 , 재시도 전략
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	@Transactional
	public void resetDailyChargeLimits() {
		try {
			mainAccountUseCase.resetAllDailyChargeAmount();
			log.info("✅ 모든 사용자 일일 충전 한도 초기화 완료");
		} catch (Exception e) {
	   		log.error("❌ 일일 충전 한도 초기화 중 오류 발생", e);
		}
	}
}
