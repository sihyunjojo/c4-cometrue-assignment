package org.c4marathon.assignment.api.transfer.validation;

import org.c4marathon.assignment.api.transfer.dto.AccountNumberTransferRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentAccountNumbersValidator implements ConstraintValidator<DifferentAccountNumbers, AccountNumberTransferRequestDto> {

    // isValid()가 false를 반환하면 → Spring이 기본 값인 MethodArgumentNotValidException을 발생시킴
    @Override
    public boolean isValid(AccountNumberTransferRequestDto value, ConstraintValidatorContext context) {
        if (value == null || value.fromAccountNumber() == null || value.toAccountNumber() == null) {
            return true; // 유효성 검사를 통과한 것으로 간주 (@NotNull 예외로 반환)
        }

        return !value.fromAccountNumber().equals(value.toAccountNumber());
    }
}
