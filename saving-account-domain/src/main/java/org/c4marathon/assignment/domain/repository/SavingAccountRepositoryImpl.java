package org.c4marathon.assignment.domain.repository;

import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.infra.persistence.jpa.JpaSavingAccountRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SavingAccountRepositoryImpl implements SavingAccountRepository {
	private final JpaSavingAccountRepository jpa;

	@Override
	public Optional<SavingAccount> findById(Long id) {
		return jpa.findById(id);
	}

	@Override
	public List<SavingAccount> findAll() {
		return jpa.findAll();
	}

	@Override
	public List<SavingAccount> findAllFixedSavingAccountWithMainAccount() {
		return jpa.findAllFixedSavingAccountWithMainAccount();
	}

	@Override
	public Optional<SavingAccount> findByIdWithoutSecondCache(Long id) {
		return jpa.findByIdWithoutSecondCache(id);
	}

	@Override
	public boolean existsByAccountNumber(String accountNumber) {
		return jpa.existsByAccountNumber(accountNumber);
	}

	@Override
	public Optional<SavingAccount> findByAccountNumber(String accountNumber) {
		return jpa.findByAccountNumber(accountNumber);
	}

	@Override
	public void save(SavingAccount account) {
		jpa.save(account);
	}

	@Override
	public int depositByOptimistic(Long accountId, Long amount, Long version) {
		return jpa.depositByOptimistic(accountId, amount, version);
	}
}
