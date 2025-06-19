package org.c4marathon.assignment.infra.persistence.repository.query;

public interface MainAccountQueryRepository {
	/**
	 * 잔액이 필요한 최소 금액 미만인 경우 주 계정에 조건부로 청구됩니다.
	 *
	 * @param id 청구할 메인 계정 ID입니다.
	 * @param amount 청구할 금액 충전 후 필요한 최소 잔액 최소 충전 잔액
	 * @param limit 일일 최대 충전 한도 반환 영향을 받는 행 수 (성공하면 1, 실패하면 0)
	 */
	boolean tryFastCharge(Long id, Long amount, Long minRequiredBalance, Long limit);
}
