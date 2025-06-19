package org.c4marathon.assignment.api.transfer.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DifferentAccountsValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface DifferentAccounts {

	String message() default "자신에게 송금할 수 없습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
