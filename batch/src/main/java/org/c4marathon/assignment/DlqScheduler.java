package org.c4marathon.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.c4marathon.assignment.usecase.dlq.DlqUsecase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DlqScheduler {

	// private static final int DLQ_THRESHOLD = 2_000_000; // 4GB 힙 메모리 기준, 200만 개면 50% 차지.
	private static final int DLQ_THRESHOLD = 100;

	private final DlqUsecase dlqUsecase;

	@Scheduled(fixedDelay = 60000) // 작업이 끝난 뒤 1분 후 실행
	@Transactional
	public void processDlqCreateTransferLogEntries() {
		log.info("DLQ 재처리 시작...");

		while (true) {
			int processedCount = dlqUsecase.processDlqBatch(50);
			log.info("이번 루프에서 처리한 개수: {}", processedCount);
			if (processedCount == 0) {
				break;
			}
		}
		log.info("DLQ 재처리 완료. 현재 DLQ 큐에 남은 개수: {}", dlqUsecase.getMQSize());
	}

	@Scheduled(cron = "0 * * * * *")
	@Transactional(readOnly = true)
	public void checkDlqMessageCount() {
		dlqUsecase.checkDlqMessageCount(DLQ_THRESHOLD);
	}

}
