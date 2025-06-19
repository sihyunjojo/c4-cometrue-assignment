package org.c4marathon.assignment.api.settlement.dto;

import jakarta.validation.constraints.NotNull;

public record SettlementResponseDto(@NotNull Long participantMemberId, @NotNull Long amount) {

}
