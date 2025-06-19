package org.c4marathon.assignment.api.transfer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = DifferentAccountNumbersValidator.class) // 이 애노테이션이 사용할 ConstraintValidator 구현 클래스를 지정
@Target({ ElementType.TYPE }) // 적용 가능한 대상을 필드가 아니라 객체 자체에 적용하기 위해 TYPE을 사용
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentAccountNumbers {
    String message() default "송신 계좌와 수신 계좌는 달라야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
