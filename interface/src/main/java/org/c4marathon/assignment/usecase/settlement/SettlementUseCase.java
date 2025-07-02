package org.c4marathon.assignment.usecase.settlement;

import java.util.List;

import org.c4marathon.assignment.domain.command.SettlementCommand;
import org.c4marathon.assignment.domain.model.Settlement;
import org.c4marathon.assignment.domain.service.SettlementService;
import org.c4marathon.assignment.api.settlement.dto.SettlementRequestDto;
import org.c4marathon.assignment.api.settlement.dto.SettlementResponseDto;
import org.c4marathon.assignment.usecase.settlement.mapper.SettlementMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementUseCase{

	private final SettlementService settlementService;
	private final SettlementMapper mapper;

	public List<SettlementResponseDto> settle(SettlementRequestDto request) {
		SettlementCommand command = mapper.toCommand(request);
		List<Settlement> settlements = settlementService.calculateSettlement(command);

		return settlements.stream()
			.map(settlement -> new SettlementResponseDto(settlement.getTargetMemberId(), settlement.getAmount()))
			.toList();
	}
}
