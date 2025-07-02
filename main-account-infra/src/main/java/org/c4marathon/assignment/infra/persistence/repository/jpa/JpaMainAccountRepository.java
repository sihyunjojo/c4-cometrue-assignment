package org.c4marathon.assignment.infra.persistence.repository.jpa;

import java.util.Optional;

import org.c4marathon.assignment.infra.persistence.entity.MainAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;

@Repository
public interface JpaMainAccountRepository extends JpaRepository<MainAccountJpaEntity, Long> {
	// @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
	// DB에서 MainAccountJpaEntity 테이블의 모든 컬럼을 가져옵니다.
	// 이걸 다시 JPA가 엔티티로 변환하고 영속성 컨텍스트에 등록하죠.
	// DB IO + 매핑 비용이 큼 → 느림
	@Query("SELECT m FROM MainAccountJpaEntity m WHERE m.member.id = :memberId")
	// 비관적락이 미세하지만 느리긴하다. (사실상 거의 동일한 성능을 보임)
	Optional<MainAccountJpaEntity> findByMemberId(@Param("memberId") Long memberId);

	// JPA의 1차 캐시는 무시 불가능.
	// 1차 캐시가 존재하면 2차 캐시는 아예 사용되지 않기 때문에, 2차 캐시만 무시해도 무조건 신선한 데이터가 보장된다고 착각하면 안 됩니다.
	// 그래서 트랜잭션을 다시 돌리고 2차 캐시 무시해서 해야함.
	// 2차 캐시 무시 코드
	@Query("SELECT m FROM MainAccountJpaEntity m WHERE m.id = :id")
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "false"),
		@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"),
		@QueryHint(name = "jakarta.persistence.cache.storeMode", value = "REFRESH")
	})
	Optional<MainAccountJpaEntity> findByIdWithoutSecondCache(@Param("id") Long id);

	// JPA 직접 업데이트를 쓸 땐 거의 무조건 필수
	// JPA가 자동으로 영속성 컨텍스트를 초기화
	// 이후 findById(id) 같은 조회를 해도 영속성 컨텍스트의 캐시가 아닌, DB의 최신값을 보장
	// 쿼리를 실행한 후, 현재 영속성 컨텍스트(1차 캐시)를 자동으로 초기화(clear) 해주는 역할
	// !! 그러나 이미 존재하던 객체는 detached 상태가 되며, 자동으로 최신 상태가 되지는 않음
	// JPA의 영속성 컨텍스트(1차 캐시)를 거치지 않는다 (JPA는 “내 객체가 바뀌었다”는 걸 모름) -> 그래서 @modifyinh(clear) 해줘야함.
	@Modifying(clearAutomatically = true)
	// JPQL 기반의 Bulk Update 쿼리
	// 영속성 컨텍스트를 거치지 않음 (DB에 직접 UPDATE만 하고, 영속성 컨텍스트에는 반영되지 않음)
	@Query("UPDATE MainAccountJpaEntity m SET m.dailyChargeAmount = 0")
	void resetAllDailyChargeAmount();

	@Modifying(clearAutomatically = true)
	@Query("UPDATE MainAccountJpaEntity a SET a.balance = a.balance + :amount, a.version = a.version + 1 "
		+ "WHERE a.id = :accountId AND a.version = :version")
	int depositByOptimistic(@Param("accountId") Long accountId, @Param("amount") Long amount,
		@Param("version") Long version);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE MainAccountJpaEntity m SET m.balance = m.balance - :amount, m.version = m.version + 1 "
		+ "WHERE m.id = :accountId AND m.balance >= :amount AND m.version = :version")
	int withdrawByOptimistic(@Param("accountId") Long accountId, @Param("amount") Long amount,
		@Param("version") Long version);

	@Query("SELECT m.balance FROM MainAccountJpaEntity m WHERE m.id = :accountId")
	Long findMainAccountJpaEntityAmountById(@Param("accountId") Long accountId);

	boolean existsByAccountNumber(String accountNumber);

	@Query("SELECT m FROM MainAccountJpaEntity m WHERE m.accountNumber = :accountNumber")
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "false"),
		@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"),
		@QueryHint(name = "jakarta.persistence.cache.storeMode", value = "REFRESH")
	})
	Optional<MainAccountJpaEntity> findByAccountNumber(@Param("accountNumber") String accountNumber);
}
