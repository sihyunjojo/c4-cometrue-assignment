package org.c4marathon.assignment.domain.service.experiment;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

// import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExperimentTransferRetryService {

	private final MainAccountRepository mainAccountRepository;

	private static final int MAX_RETRY = 5;
	private static final long BASE_SLEEP_TIME_MS = 100;
	private static final long MAX_SLEEP_TIME_MS = 2000;

	// 재사용할 트랜잭션 템플릿 미리 생성
	private final TransactionTemplate readOnlyTemplate;
	private final TransactionTemplate requiresNewTemplate;

	// 생성자 주입으로 트랜잭션 템플릿 초기화
	public ExperimentTransferRetryService(
		MainAccountRepository mainAccountRepository,
		PlatformTransactionManager transactionManager) {
		this.mainAccountRepository = mainAccountRepository;

		// 읽기 전용 트랜잭션 템플릿
		this.readOnlyTemplate = new TransactionTemplate(transactionManager);
		this.readOnlyTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.readOnlyTemplate.setReadOnly(true);

		// 쓰기 트랜잭션 템플릿
		this.requiresNewTemplate = new TransactionTemplate(transactionManager);
		this.requiresNewTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	// 재시도 가능한 예외 목록
	private static final List<Class<? extends Throwable>> RETRYABLE_EXCEPTIONS = Arrays.asList(
		OptimisticLockException.class,
		ObjectOptimisticLockingFailureException.class,
		OptimisticLockingFailureException.class,
		// MySQLTransactionRollbackException.class,
		DataAccessException.class,
		TransactionSystemException.class
	);

	/**
	 * 이체를 재시도 로직과 함께 수행
	 * 성능보다 정합성이 우선되어야함.
	 */
	@Transactional
	public void transferWithRetry(Long fromAccountId, Long toAccountId, Long transferAmount) {
		// 출금 처리
		executeWithRetry(() -> {
			MainAccount fromAccount = getRefreshedAccount(fromAccountId);
			withdraw(fromAccount, transferAmount);
			return null;
		});

		// 입금 처리
		executeWithRetry(() -> {
			MainAccount toAccount = getRefreshedAccount(toAccountId);
			deposit(toAccount, transferAmount);
			return null;
		});
	}

	/**
	 * 재시도 로직을 통한 함수 실행
	 */
	private <T> T executeWithRetry(Callable<T> operation) {
		int attempts = 0;
		Exception lastException = null;

		while (attempts < MAX_RETRY) {
			try {
				T result = operation.call();
				return result;
			} catch (Exception e) {
				if (isRetryableException(e)) {
					attempts++;
					lastException = e;

					if (attempts >= MAX_RETRY) {
						log.error("최대 재시도 횟수 초과: {}", e.getMessage());
						break;
					}

					log.warn("처리 중 오류 발생. 재시도 {}/{}: {}", attempts, MAX_RETRY, e.getMessage());
					sleepWithBackoff(attempts);

					// 전체 컨텍스트를 지우지 않고 필요한 경우 새로운 트랜잭션에서 처리
				} else {
					throw new RuntimeException("재시도 불가능한 비즈니스 예외 발생", e);
				}
			}
		}

		throw new RuntimeException("최대 재시도 횟수 초과", lastException);
	}

	private void sleepWithBackoff(int retryCount) {
		try {
			// 최적화된 지수 백오프 알고리즘
			long sleep = Math.min(BASE_SLEEP_TIME_MS * (1L << (retryCount - 1)), MAX_SLEEP_TIME_MS);
			// 더 작은 지터(무작위성) 적용
			sleep += ThreadLocalRandom.current().nextLong(0, 50);
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("스레드 인터럽트 발생", e);
		}
	}

	/**
	 * 최신 계좌 정보 조회
	 */
	// readOnlyTemplate에 requieds_new로 jpa 1차 캐싱된게 아닌 새로운걸 항상 가져오도록 + readOnly피하기
	private MainAccount getRefreshedAccount(Long accountId) {
		return readOnlyTemplate.execute(status ->
			mainAccountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalStateException("ID가 " + accountId + "인 메인 계좌가 존재하지 않습니다."))
		);
	}

	private boolean isRetryableException(Exception e) {
		// 예외의 근본 원인까지 검사
		Throwable current = e;
		while (current != null) {

			if (current instanceof SQLException sqlEx) {
				int errorCode = sqlEx.getErrorCode();
				// ORA-08177: can't serialize access for this transaction
				if (errorCode == 8177) return true;

				// ORA-00060: deadlock detected
				if (errorCode == 60) return true;
			}

			for (Class<? extends Throwable> exceptionClass : RETRYABLE_EXCEPTIONS) {
				if (exceptionClass.isInstance(current)) {
					return true;
				}
			}
			current = current.getCause();
		}
		return false;
	}

	private void withdraw(MainAccount from, Long amount) {
		requiresNewTemplate.execute(status -> {
			int withdrawResult = mainAccountRepository.withdrawByOptimistic(
				from.getId(),
				amount,
				from.getVersion()
			);

			if (withdrawResult == 0) {
				throw new OptimisticLockingFailureException("출금 처리 중 충돌이 발생했습니다.");
			}
			return true;
		});
	}

	private void deposit(MainAccount to, Long amount) {
		requiresNewTemplate.execute(status -> {
			int depositResult = mainAccountRepository.depositByOptimistic(
				to.getId(),
				amount,
				to.getVersion()
			);

			if (depositResult == 0) {
				throw new OptimisticLockingFailureException("입금 처리 중 충돌이 발생했습니다.");
			}
			return true;
		});
	}
}
