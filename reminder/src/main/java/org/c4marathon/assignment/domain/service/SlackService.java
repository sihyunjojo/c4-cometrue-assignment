package org.c4marathon.assignment.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SlackService {

	@Value("${slack.token}")
	private String slackToken;

	@Value("${slack.channel}")
	private String slackChannel;

	public void sendSlackNotification(long dlqThreshold, long currentMessageCount) {
		try {
			MethodsClient methods = Slack.getInstance().methods(slackToken);
			String message = String.format("DLQ 메시지 수는 %d의 임계 값을 초과했습니다. 현재 수 : %d", dlqThreshold, currentMessageCount);

			ChatPostMessageRequest request = ChatPostMessageRequest.builder()
				.channel(slackChannel)
				.text(message)
				.build();

			methods.chatPostMessage(request);
		} catch (Exception e) {
			log.error("슬랙 알림 실패: {}", e.getMessage());
		}
	}
}
