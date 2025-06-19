package org.c4marathon.assignment.api.transfer.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * DifferentAccounts 어노테이션의 유효성 검사를 수행하는 Validator 클래스입니다.
 * AccountHolder 인터페이스를 구현한 모든 DTO에 대해 fromAccountId와 toAccountId가 다른지 검증합니다.
 */
public class DifferentAccountsValidator implements ConstraintValidator<DifferentAccounts, AccountHolder> {

    @Override
    public boolean isValid(AccountHolder value, ConstraintValidatorContext context) {
        if (value == null || value.fromAccountId() == null || value.toAccountId() == null) {
            return true; // @NotNull 등의 다른 검증에서 처리할 문제이므로 여기서는 true 반환
        }
        return !value.fromAccountId().equals(value.toAccountId());
    }
}
