package org.c4marathon.assignment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoDuplicateElementsValidator implements ConstraintValidator<NoDuplicateElements, List<?>> {

	@Override
	public boolean isValid(List<?> value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // @NotEmpty로 별도 처리
		}

		Set<Object> set = new HashSet<>();
		for (Object element : value) {
			if (!set.add(element)) {
				return false; // 이미 있는 값을 또 추가하려 하면 false
			}
		}
		return true;
	}
}
