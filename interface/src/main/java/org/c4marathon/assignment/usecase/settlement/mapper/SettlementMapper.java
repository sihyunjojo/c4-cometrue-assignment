package org.c4marathon.assignment.usecase.settlement.mapper;

import org.c4marathon.assignment.api.settlement.dto.SettlementRequestDto;
import org.c4marathon.assignment.domain.command.SettlementCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettlementMapper {

	SettlementCommand toCommand(SettlementRequestDto request);

}
