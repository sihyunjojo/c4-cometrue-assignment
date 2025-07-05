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
public class DlqRetryBatchScheduler {

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
}
