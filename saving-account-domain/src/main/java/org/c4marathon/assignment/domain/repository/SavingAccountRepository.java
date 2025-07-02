package org.c4marathon.assignment.domain.repository;

import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.domain.model.SavingAccount;

public interface SavingAccountRepository {

    Optional<SavingAccount> findById(Long id);

    List<SavingAccount> findAll();

    List<SavingAccount> findAllFixedSavingAccountWithMainAccount();

    Optional<SavingAccount> findByIdWithoutSecondCache(Long id);

    boolean existsByAccountNumber(String accountNumber);

    Optional<SavingAccount> findByAccountNumber(String accountNumber);

    void save(SavingAccount account);

    int depositByOptimistic(Long accountId, Long amount, Long version);

}
