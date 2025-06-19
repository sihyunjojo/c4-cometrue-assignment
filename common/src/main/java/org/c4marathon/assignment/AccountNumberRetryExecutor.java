package org.c4marathon.assignment;

import java.util.concurrent.Callable;

import org.c4marathon.assignment.exception.RetryableException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberRetryExecutor {

	private static final int MAX_RETRY = 10; // 재시도는 10번 유지

	public <T> T executeWithRetry(Callable<T> operation) {
		int attempts = 0;
		Exception lastException = null;

		while (attempts < MAX_RETRY) {
			try {
				return operation.call();
			} catch (Exception e) {
				if (isRetryableException(e)) {
					attempts++;
					lastException = e;

					if (attempts >= MAX_RETRY) {
						break;
					}

					log.warn("계좌번호 생성 재시도 {}/{}: {}", attempts, MAX_RETRY, e.getMessage());

				} else {
					throw new RuntimeException(String.format("재시도 불가능한 예외 발생: %s", e.getMessage()), e);
				}
			}
		}

		throw new RuntimeException("계좌번호 생성 최대 재시도 횟수 초과", lastException);
	}

	private boolean isRetryableException(Exception e) {
		return e instanceof RetryableException;
	}
}
