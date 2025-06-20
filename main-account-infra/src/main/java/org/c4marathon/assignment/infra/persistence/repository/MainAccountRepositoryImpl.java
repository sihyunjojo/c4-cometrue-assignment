package org.c4marathon.assignment.infra.persistence.repository;

import java.util.Optional;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.repository.MainAccountRepository;
import org.c4marathon.assignment.infra.persistence.entity.MainAccountJpaEntity;
import org.c4marathon.assignment.infra.persistence.entity.MemberJpaEntity;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMainAccountRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMemberRepository;
import org.c4marathon.assignment.infra.persistence.repository.query.MainAccountQueryRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MainAccountRepositoryImpl implements MainAccountRepository {

	private final JpaMainAccountRepository jpaMainAccountRepository;
	private final JpaMemberRepository jpaMemberRepository;
	private final MainAccountQueryRepository query;

	@Override
	public MainAccount save(MainAccount mainAccount) {
		MemberJpaEntity memberJpaEntity = jpaMemberRepository.findById(mainAccount.getMemberId())
		.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. memberId: " + mainAccount.getMemberId()));
        
        MainAccountJpaEntity jpaEntity = MainAccountJpaEntity.fromDomain(mainAccount, memberJpaEntity);
        MainAccountJpaEntity savedEntity = jpaMainAccountRepository.save(jpaEntity);
        
        return savedEntity.toDomain();
	}

	@Override
	public Optional<MainAccount> findById(Long id) {
		return jpaMainAccountRepository.findById(id)
			.map(MainAccountJpaEntity::toDomain);
	}

	@Override
	public Optional<MainAccount> findByIdWithoutSecondCache(Long id) {
		return jpaMainAccountRepository.findByIdWithoutSecondCache(id)
			.map(MainAccountJpaEntity::toDomain);
	}

	@Override
	public Optional<MainAccount> findByMemberId(Long memberId) {
		return jpaMainAccountRepository.findByMemberId(memberId)
			.map(MainAccountJpaEntity::toDomain);
	}

	@Override
	public Optional<MainAccount> findByAccountNumber(String accountNumber) {
		return jpaMainAccountRepository.findByAccountNumber(accountNumber)
			.map(MainAccountJpaEntity::toDomain);
	}

	@Override
	public boolean existsByAccountNumber(String accountNumber) {
		return jpaMainAccountRepository.existsByAccountNumber(accountNumber);
	}

	@Override
	public void resetAllDailyChargeAmount() {
		jpaMainAccountRepository.resetAllDailyChargeAmount();
	}

	@Override
	public int depositByOptimistic(Long id, Long amount, Long version) {
		return jpaMainAccountRepository.depositByOptimistic(id, amount, version);
	}

	@Override
	public int withdrawByOptimistic(Long id, Long amount, Long version) {
		return jpaMainAccountRepository.withdrawByOptimistic(id, amount, version);	
	}

	@Override
	public Long findMainAccountAmountById(Long id) {
		return jpaMainAccountRepository.findMainAccountJpaEntityAmountById(id);
	}

	@Override
	public boolean tryFastCharge(Long id, Long amount, Long minRequiredBalance, Long dailyLimit) {
		return query.tryFastCharge(id, amount, minRequiredBalance, dailyLimit);
	}
}
