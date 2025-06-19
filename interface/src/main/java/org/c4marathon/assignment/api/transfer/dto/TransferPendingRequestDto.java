package org.c4marathon.assignment.api.transfer.dto;

import org.c4marathon.assignment.api.transfer.validation.DifferentAccounts;
import org.c4marathon.assignment.api.transfer.validation.AccountHolder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@DifferentAccounts(message = "자기 자신에게 송금할 수 없습니다.")
public record TransferPendingRequestDto(

	@NotNull
	@Schema(description = "송신 계좌 ID", example = "1")
	Long fromAccountId,

	@NotNull
	@Schema(description = "수신 계좌 ID", example = "2")
	Long toAccountId,

	@NotNull
	@Positive(message = "송금 금액은 0보다 커야 합니다.")
	@Schema(description = "송금 금액 (단위: 원)", example = "50000")
	Long amount

) implements AccountHolder {}
