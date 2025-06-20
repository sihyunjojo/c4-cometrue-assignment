package org.c4marathon.assignment.api.transfer.dto;

import org.c4marathon.assignment.api.transfer.validation.AccountHolder;
import org.c4marathon.assignment.api.transfer.validation.DifferentAccounts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
@DifferentAccounts(message = "자신에게 송금할 수 없습니다.")
public record TransferRequestDto(

	@Schema(description = "송신 계좌 ID", example = "1")
	@NotNull(message = "보내는 계좌 ID는 필수입니다.")
	Long fromAccountId,

	@Schema(description = "수신 계좌 ID", example = "2")
	@NotNull(message = "받는 계좌 ID는 필수입니다.")
	Long toAccountId,

	@Schema(description = "송금 금액 (단위: 원)", example = "11111")
	@NotNull(message = "송금 금액은 필수입니다.")
	@Positive(message = "음수는 송금할 수 없습니다.")
	Long amount
) implements AccountHolder {
}
