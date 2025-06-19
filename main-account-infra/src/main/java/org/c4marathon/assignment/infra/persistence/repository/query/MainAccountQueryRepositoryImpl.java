package org.c4marathon.assignment.infra.persistence.repository.query;

import static org.c4marathon.assignment.infra.persistence.entity.QMainAccountJpaEntity.mainAccountJpaEntity;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MainAccountQueryRepositoryImpl implements MainAccountQueryRepository {

	private final JPAQueryFactory queryFactory;

	// 동시 요청이 발생할 수 있기 때문이야.
	// 애플리케이션에서 from.getBalance()로 확인한 시점과
	// DB에서 UPDATE가 실제로 일어나는 시점 사이에는 시간 차이가 존재해.
	// 그 사이에 다른 트랜잭션이 balance를 바꿔버릴 수 있어.
	// Pre-check in app + assert in DB 패턴 (내가 보기엔 괜찮아 보여도, DB가 최종 판단한다)
	// 포인트, 쿠폰, 돈처럼 정합성이 중요한 도메인에서 거의 필수로 사용
	// 이 조건은 비관적 락보다 가볍다
	// 이 방식을 쓰면 낙관적,비관적락을 안쓰고 그냥 락을 안걸어도 정합성을 유지시켜줌.
	@Override
	public boolean tryFastCharge(Long id, Long amount, Long minRequiredBalance, Long dailyLimit) {
		return queryFactory
			.update(mainAccountJpaEntity)
			.set(mainAccountJpaEntity.balance, mainAccountJpaEntity.balance.add(amount))
			.set(mainAccountJpaEntity.dailyChargeAmount, mainAccountJpaEntity.dailyChargeAmount.add(amount))
			.where(
				mainAccountJpaEntity.id.eq(id),
				mainAccountJpaEntity.balance.add(amount).goe(minRequiredBalance),
				mainAccountJpaEntity.dailyChargeAmount.add(amount).loe(dailyLimit)
			)
			.execute() > 0;
	}
}
