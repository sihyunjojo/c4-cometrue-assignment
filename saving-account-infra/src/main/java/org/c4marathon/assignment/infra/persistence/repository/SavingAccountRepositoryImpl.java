package org.c4marathon.assignment.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.c4marathon.assignment.domain.model.SavingAccount;
import org.c4marathon.assignment.domain.repository.SavingAccountRepository;
import org.c4marathon.assignment.infra.persistence.entity.MainAccountJpaEntity;
import org.c4marathon.assignment.infra.persistence.entity.MemberJpaEntity;
import org.c4marathon.assignment.infra.persistence.entity.SavingAccountJpaEntity;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMainAccountRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMemberRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaSavingAccountRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SavingAccountRepositoryImpl implements SavingAccountRepository {
    private final JpaSavingAccountRepository jpaSavingAccountRepository;
    private final JpaMemberRepository jpaMemberRepository;
    private final JpaMainAccountRepository jpaMainAccountRepository;

    @Override
    public Optional<SavingAccount> findById(Long id) {
        return jpaSavingAccountRepository.findById(id)
            .map(SavingAccountJpaEntity::toDomain);
    }

    @Override
    public List<SavingAccount> findAll() {
        return jpaSavingAccountRepository.findAll().stream()
            .map(SavingAccountJpaEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SavingAccount> findAllFixedSavingAccountWithMainAccount() {
        return jpaSavingAccountRepository.findAllFixedSavingAccountWithMainAccount().stream()
            .map(SavingAccountJpaEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<SavingAccount> findByIdWithoutSecondCache(Long id) {
        return jpaSavingAccountRepository.findByIdWithoutSecondCache(id)
            .map(SavingAccountJpaEntity::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaSavingAccountRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public Optional<SavingAccount> findByAccountNumber(String accountNumber) {
        return jpaSavingAccountRepository.findByAccountNumber(accountNumber)
            .map(SavingAccountJpaEntity::toDomain);
    }

    @Override
    public void save(SavingAccount account) {
        MemberJpaEntity member = jpaMemberRepository.findById(account.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("회원의 ID가 없습니다: " + account.getMemberId()));
            
        MainAccountJpaEntity mainAccount = null;
        if (account.getMainAccountId() != null) {
            mainAccount = jpaMainAccountRepository.findById(account.getMainAccountId())
                .orElseThrow(() -> new IllegalArgumentException("메인 계좌의 ID가 없습니다: " + account.getMainAccountId()));
        }
        
        SavingAccountJpaEntity jpaEntity = SavingAccountJpaEntity.fromDomain(account, member, mainAccount);
        jpaSavingAccountRepository.save(jpaEntity);
    }

    @Override
    public int depositByOptimistic(Long accountId, Long amount, Long version) {
        return jpaSavingAccountRepository.depositByOptimistic(accountId, amount, version);
    }
}
