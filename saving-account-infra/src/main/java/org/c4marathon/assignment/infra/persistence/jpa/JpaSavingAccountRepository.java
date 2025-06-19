package org.c4marathon.assignment.infra.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;

public interface JpaSavingAccountRepository extends JpaRepository<SavingAccount, Long> {
	@Query("SELECT sa FROM SavingAccount sa " +
		"JOIN FETCH sa.mainAccount " +
		"WHERE sa.savingType = 'FIXED'")
	List<SavingAccount> findAllFixedSavingAccountWithMainAccount();

	@Query("SELECT s FROM SavingAccount s WHERE s.id = :id")
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "false"),
		@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"),
		@QueryHint(name = "jakarta.persistence.cache.storeMode", value = "REFRESH")
	})
	Optional<SavingAccount> findByIdWithoutSecondCache(@Param("id") Long id);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE SavingAccount a SET a.balance = a.balance + :amount, a.version = a.version + 1 "
		+ "WHERE a.id = :accountId AND a.version = :version")
	int depositByOptimistic(@Param("accountId") Long accountId, @Param("amount") Long amount,
		@Param("version") Long version);

	boolean existsByAccountNumber(String accountNumber);

	@Query("SELECT sa FROM SavingAccount sa WHERE sa.accountNumber = :accountNumber")
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "false"),
		@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"),
		@QueryHint(name = "jakarta.persistence.cache.storeMode", value = "REFRESH")
	})
	Optional<SavingAccount> findByAccountNumber(@Param("accountNumber") String accountNumber);

}
