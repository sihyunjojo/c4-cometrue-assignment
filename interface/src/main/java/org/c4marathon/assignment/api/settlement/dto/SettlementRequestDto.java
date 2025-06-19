package org.c4marathon.assignment.api.settlement.dto;

import java.util.List;

import org.c4marathon.assignment.model.policy.SettlementPolicy;
import org.c4marathon.assignment.validation.NoDuplicateElements;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SettlementRequestDto(

	@NotNull
	@Schema(description = "정산 방식", defaultValue = "EQUAL", allowableValues = {"EQUAL", "RANDOM"})
	SettlementPolicy policyType,

	@NotNull
	@Positive(message = "정산 금액은 0보다 커야 합니다.")
	@Schema(description = "전체 정산 금액", example = "20000")
	Long totalAmount,

	@NotNull
	@NotEmpty(message = "참여자 ID 목록은 비어 있을 수 없습니다.")
	@NoDuplicateElements(message = "참여자 ID 목록에 중복된 값이 있습니다.")
	@Schema(description = "참여자 ID 목록", example = "[1,2,3]")
	List<Long> participantMemberIdList

) {}
