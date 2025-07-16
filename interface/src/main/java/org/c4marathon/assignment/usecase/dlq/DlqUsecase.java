package org.c4marathon.assignment.usecase.dlq;

import static org.springframework.transaction.annotation.Propagation.*;

import java.util.List;

import org.c4marathon.assignment.domain.model.DlqEntry;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.DlqEntryService;
import org.c4marathon.assignment.domain.service.SlackService;
import org.c4marathon.assignment.domain.service.TransferLogService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqUsecase {

	private final static int MAX_RETRIES = 3;

	private final DlqEntryService dlqEntryService;
	private final SlackService slackService;
	private final ObjectMapper objectMapper;
	private final TransferLogService transferLogService;

	@Transactional(propagation = REQUIRES_NEW)
	public void saveDLQ(Exception e, TransferLog transferLog) {
		try {
			String payload = objectMapper.writeValueAsString(transferLog);
			DlqEntry dlqEntry = new DlqEntry("CREATE_TRANSFER_LOG", payload, e.getMessage());
			dlqEntryService.save(dlqEntry);
		} catch (Exception dlqException) {
			// DLQ 저장 실패는 복구 불가능한 심각한 오류이므로, 강력하게 전파
			log.error("CRITICAL: DLQ 저장 최종 실패. 데이터 유실 가능성 높음.", dlqException);
			throw new RuntimeException("DLQ 저장에 실패하여 데이터가 유실될 수 있습니다.", dlqException);
		}
	}

	/**
	 * PENDING 상태의 DLQ 엔트리를 limit개씩 가져와 처리
	 */
	@Transactional
	public int processDlqBatch(int limit) {
		List<DlqEntry> pendingEntries = dlqEntryService.findPendingCreateTransferLogEntries(limit);
		if (pendingEntries.isEmpty()) {
			return 0;
		}
		pendingEntries.forEach(this::processSingleDlqEntry);
		return pendingEntries.size();
	}

	public int getMQSize() {
		return dlqEntryService.getMQSize();
	}

	public void checkDlqMessageCount(int dlqThreshold) {
		long currentMessageCount = dlqEntryService.getMQSize();

		if (currentMessageCount > dlqThreshold) {
			log.warn("DLQ 메시지 수 임계 값({})를 초과했습니다 . 현재 메시지 수: {}", dlqThreshold, currentMessageCount);
			slackService.sendSlackNotification(dlqThreshold, currentMessageCount);
		}
	}

	private void processSingleDlqEntry(DlqEntry entry) {
		try {
			// 재시도 횟수 초과 시 영구 실패 처리
			if (entry.getRetryCount() >= MAX_RETRIES) {
				entry.markAsFailed();
				dlqEntryService.markAsProcessedAndCleanUp(entry);
				log.warn("DLQ 엔트리 재시도 횟수 초과, 영구 실패 처리: ID = {}", entry.getId());
				return;
			}

			entry.markAsProcessing();
			dlqEntryService.update(entry);

			if ("CREATE_TRANSFER_LOG".equals(entry.getOperationType())) {
				try {
					// payload를 TransferLog 객체로 역직렬화
					TransferLog transferLog = objectMapper.readValue(entry.getPayload(), TransferLog.class);
					transferLogService.saveTransferLog(transferLog);
					log.info("송금 이력 생성 재시도 성공: Payload = {}", entry.getPayload());
					entry.markAsSuccess();
					dlqEntryService.markAsProcessedAndCleanUp(entry);
				} catch (JsonProcessingException e) {
					log.error("TransferLog 역직렬화 실패: {}", e.getMessage(), e);
					entry.markAsFailed();
					dlqEntryService.markAsProcessedAndCleanUp(entry);
				}
			} else {
				log.warn("알 수 없는 DLQ Operation Type: {}", entry.getOperationType());
				entry.markAsFailed();
				dlqEntryService.markAsProcessedAndCleanUp(entry);
			}

			log.info("DLQ 엔트리 처리 완료: ID = {}", entry.getId());

		} catch (Exception e) {
			log.error("DLQ 엔트리 처리 중 오류 발생: ID = {}, Error = {}", entry.getId(), e.getMessage(), e);
			entry.incrementRetryCount();
			entry.calculateNextRetryTime();
			entry.markAsPending();
			dlqEntryService.markAsProcessedAndCleanUp(entry);
		}
	}
}
