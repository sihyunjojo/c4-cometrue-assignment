package org.c4marathon.assignment.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingRetryListener implements RetryListener {

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		// log.debug("🔁 Retry 시작");
		return true; // false를 반환하면 retry 자체를 막음
	}

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
		// log.warn("⚠️ Retry 실패: 현재 시도 횟수 = {}, 예외 = {}", context.getRetryCount(), throwable.toString());
	}

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
		if (throwable == null) {
			log.info("✅ Retry 성공: 총 시도 횟수 = {}", context.getRetryCount());
		} else {
			log.error("❌ Retry 최종 실패: 총 시도 횟수 = {}, 최종 예외 = {}", context.getRetryCount(), throwable.toString());
		}
	}
}
