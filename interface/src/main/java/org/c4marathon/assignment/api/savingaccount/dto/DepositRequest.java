package org.c4marathon.assignment.api.savingaccount.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequest(@NotNull(message = "적금 계좌 ID는 필수입니다.") Long savingAccountId,
							 @NotNull(message = "금액은 필수입니다.") @Positive(message = "금액은 0보다 커야 합니다.") Long amount) {
}
