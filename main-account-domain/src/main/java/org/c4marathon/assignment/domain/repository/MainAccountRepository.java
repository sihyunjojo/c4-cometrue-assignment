package org.c4marathon.assignment.domain.repository;

import org.c4marathon.assignment.domain.model.MainAccount;

import java.util.Optional;

public interface MainAccountRepository {
	MainAccount save(MainAccount mainAccount);

	Optional<MainAccount> findById(Long id);

	Optional<MainAccount> findByIdWithoutSecondCache(Long id);

	Optional<MainAccount> findByMemberId(Long memberId);

	Optional<MainAccount> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);

	void resetAllDailyChargeAmount();

	int depositByOptimistic(Long id, Long amount, Long version);

	int withdrawByOptimistic(Long id, Long amount, Long version);

	Long findMainAccountAmountById(Long id);

	boolean tryFastCharge(Long id, Long amount, Long minRequiredBalance, Long dailyLimit);
}
