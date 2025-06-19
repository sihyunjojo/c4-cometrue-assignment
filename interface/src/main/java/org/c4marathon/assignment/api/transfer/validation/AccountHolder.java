package org.c4marathon.assignment.api.transfer.validation;

/**
 * 계좌 송금 요청 DTO들이 구현해야 하는 인터페이스입니다.
 * fromAccountId와 toAccountId를 가진 DTO는 이 인터페이스를 구현해야 합니다.
 */
public interface AccountHolder {
    Long fromAccountId();
    Long toAccountId();
}
