package org.c4marathon.assignment.api.transfer.dto;

import org.c4marathon.assignment.api.transfer.validation.DifferentAccountNumbers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@DifferentAccountNumbers(message = "자신에게 송금할 수 없습니다.")
public record AccountNumberTransferRequestDto(

    @Schema(description = "송신 계좌 번호", example = "1234-5678-9012")
    @NotBlank(message = "보내는 계좌 번호는 필수입니다.")
    String fromAccountNumber,

    @Schema(description = "수신 계좌 번호", example = "2345-6789-0123")
    @NotBlank(message = "받는 계좌 번호는 필수입니다.")
    String toAccountNumber,

    @Schema(description = "송금 금액 (단위: 원)", example = "11111")
    @NotNull(message = "송금 금액은 필수입니다.")
    @PositiveOrZero(message = "음수는 송금할 수 없습니다.")
    Long amount
) {}
