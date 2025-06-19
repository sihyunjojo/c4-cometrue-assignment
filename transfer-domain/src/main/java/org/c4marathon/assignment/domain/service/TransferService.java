package org.c4marathon.assignment.domain.service;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.c4marathon.assignment.domain.repository.SavingAccountRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

	private final PlatformTransactionManager transactionManager;

	private final MainAccountRepository mainAccountRepository;
	private final SavingAccountRepository savingAccountRepository;

	/**
	 * 새로운 트랜잭션에서 이체 수행
	 */
	// 이전 트랜잭션의 rollback-only 상태를 회피하려는 의도 (원래 아래 트랜잭션으로 하려했지만, 이후 리트라이 로직으로 변경) (requrieds_new 삭제)
	// 상위 트랜잭션이 아직 종료되지 않아 락을 보유 중인 상태에서, 하위 메서드가 새로운 트랜잭션(REQUIRES_NEW) 으로 동일 자원에 접근하면서 락 충돌이 발생
	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void transferInNewTransaction(MainAccount from, MainAccount to, Long amount) {
		// Spring AOP의 한계 때문에 직접 설정
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		template.execute(status -> {
			try {
				// 출금
				withdraw(from, amount);
				// 입금
				deposit(to, amount);
				// 성공 → COMMIT됨
				return true;
			} catch (Exception e) {
				// 여기서 예외가 터지면 상위 트랜잭션에게 롤백을 무조건 하라고 명령
				// TransactionTemplate은 내부적으로 RuntimeException이나 Error가 아닌 예외가 던져지면 rollback을 안 합니다.
				// 그래서 catch 안에서 rollback을 명시적으로 강제해야 예외가 생겼을 때 rollback 되죠.
				status.setRollbackOnly();
				throw e;
			}
		});
	}

	public void transferInNewTransaction(MainAccount from, SavingAccount to, Long amount) {
		// Spring AOP의 한계 때문에 직접 설정
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		template.execute(status -> {
			try {
				// 출금
				withdraw(from, amount);
				// 입금
				deposit(to, amount);
				// 성공 → COMMIT됨
				return true;
			} catch (Exception e) {
				// 여기서 예외가 터지면 상위 트랜잭션에게 롤백을 무조건 하라고 명령
				// TransactionTemplate은 내부적으로 RuntimeException이나 Error가 아닌 예외가 던져지면 rollback을 안 합니다.
				// 그래서 catch 안에서 rollback을 명시적으로 강제해야 예외가 생겼을 때 rollback 되죠.
				status.setRollbackOnly();
				throw e;
			}
		});
	}

	private void withdraw(MainAccount from, Long amount) {
		int withdrawResult = mainAccountRepository.withdrawByOptimistic(from.getId(), amount, from.getVersion());

		if (withdrawResult == 0) {
			throw new OptimisticLockingFailureException("출금 처리 중 충돌이 발생했습니다.");
		}
	}

	private void deposit(MainAccount to, Long amount) {
		int depositResult = mainAccountRepository.depositByOptimistic(to.getId(), amount, to.getVersion());

		if (depositResult == 0) {
			throw new OptimisticLockingFailureException("입금 처리 중 충돌이 발생했습니다.");
		}
	}

	private void deposit(SavingAccount to, Long amount) {
		int depositResult = savingAccountRepository.depositByOptimistic(to.getId(), amount, to.getVersion());

		if (depositResult == 0) {
			throw new OptimisticLockingFailureException("입금 처리 중 충돌이 발생했습니다.");
		}
	}
}
