package org.c4marathon.assignment.retry;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RetryExecutor {

	private static final int MAX_RETRY = 3;
	private static final long BASE_SLEEP_TIME_MS = 300;      // 초기 대기 시간 : 정상 요청이 20~50ms라면 300ms 정도면 대부분의 락이 풀렸을 확률 높음 -> 동시에 같은 계좌를 접근할 가능성이 높은 서비스면 300~600ms 이상 추천
	private static final long MAX_SLEEP_TIME_MS = 2000;

	// private final ConcurrentLinkedQueue<Integer> retryAttemptHistory = new ConcurrentLinkedQueue<>(); 	// 멀티스레드 환경에서 동시성 문제 없이 다음을 수행하고 싶을 때:

	private final EntityManager entityManager;
	private final TransactionTemplate newTransactionTemplate;

	public RetryExecutor(EntityManager entityManager, TransactionTemplate transactionTemplate) {
		this.entityManager = entityManager;
		this.newTransactionTemplate = transactionTemplate;
		this.newTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	// // 재시도 로직까지 하나의 트랜잭션으로 묶여서 예외가 터진것을 롤백할 상황이라고 생각
	// // 1. @Transactional 메서드 진입 → 트랜잭션 시작
	// // 2. 중간에 예외 발생 → rollback-only 플래그 설정
	// // 3. catch로 예외를 처리하더라도 rollback-only는 유지됨
	// // 4. 메서드 종료 후 커밋 시도 → 이미 rollback-only라서 UnexpectedRollbackException 발생
	public <T> T executeWithRetry(Callable<T> operation) {
		AtomicInteger attempts = new AtomicInteger();
		AtomicReference<Exception> lastException = new AtomicReference<>();
		ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();

		while (attempts.get() < MAX_RETRY) {
			try {
				T result = newTransactionTemplate.execute(status -> {
					try {
						attempts.getAndIncrement();
						return operation.call();
					} catch (Exception e) {
						Throwable cause = e.getCause();
						Exception actualException = (cause instanceof Exception) ? (Exception)cause : e;
						exceptions.add(actualException);
						lastException.set(e);

						if (isRetryableException(actualException)) {
							log.warn("이체 처리 중 오류 발생. 재시도 {}/{}: {}", attempts, MAX_RETRY, e.getMessage());
							sleepWithBackoff(attempts.get());
							// 엔티티 매니저 초기화 - 현재 컨텍스트에서 캐시된 엔티티 제거
							entityManager.clear();
							// 여기서 예외가 터지면 상위 트랜잭션에게 롤백을 무조건 하라고 명령
							// TransactionTemplate은 내부적으로 RuntimeException이나 Error가 아닌 예외가 던져지면 rollback을 안 합니다.
							// 그래서 catch 안에서 rollback을 명시적으로 강제해야 예외가 생겼을 때 rollback 되죠.
							// 반드시 rollback 처리
							status.setRollbackOnly();
							return null;
						} else {
							throw new RuntimeException(String.format("재시도 불가능한 예외 발생 : %s", e.getMessage()), e);
						}
					}
				});
				if (result != null) {
					return result;
				}
			} catch (RuntimeException re) {
				throw re;
			}
		}

		throw new RuntimeException("최대 재시도 횟수 초과. 예외 목록: " +
			exceptions.stream()
				.map(Throwable::getMessage)
				.collect(Collectors.joining(" | ")),
			lastException.get()
		);
	}

	private void sleepWithBackoff(int retryCount) {
		try {
			long exponential = BASE_SLEEP_TIME_MS * (1L << retryCount);  // 지수 백오프
			long jitter = ThreadLocalRandom.current().nextLong(BASE_SLEEP_TIME_MS); // 0~100
			long sleep = Math.min(exponential + jitter, MAX_SLEEP_TIME_MS);
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("스레드 인터럽트 발생", e);
		}
	}

	private boolean isRetryableException(Exception e) {
		return e instanceof OptimisticLockException || e instanceof DataAccessException
			|| e instanceof TransactionSystemException;
	}
}
