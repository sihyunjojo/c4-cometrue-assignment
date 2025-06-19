package org.c4marathon.assignment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NoDuplicateElementsValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface NoDuplicateElements {

	String message() default "목록에 중복된 값이 존재합니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
