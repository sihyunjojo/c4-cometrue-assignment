package org.c4marathon.assignment.api.savingaccount.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "정기 적금 계좌 생성 요청 DTO")
public record CreateFixedSavingAccountRequestDto(
	@Schema(description = "정기 입금 금액(원)", example = "1000")
	Long subscribedDepositAmount
) {}
