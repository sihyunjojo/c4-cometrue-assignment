package org.c4marathon.assignment.domain.service;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.c4marathon.assignment.domain.repository.SavingAccountRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

	private final MainAccountRepository mainAccountRepository;
	private final SavingAccountRepository savingAccountRepository;

	public void transfer(MainAccount from, MainAccount to, Long amount) {
		withdraw(from, amount);
		deposit(to, amount);
	}

	public void transfer(MainAccount from, SavingAccount to, Long amount) {
		withdraw(from, amount);
		deposit(to, amount);
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
