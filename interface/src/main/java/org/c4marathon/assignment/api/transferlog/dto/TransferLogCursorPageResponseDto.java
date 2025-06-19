package org.c4marathon.assignment.api.transferlog.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TransferLogCursorPageResponseDto(

	@Schema(description = "송금 이력 목록")
	List<TransferLogDto> data,

	@Schema(
		description = "다음 페이지 존재 여부",
		defaultValue = "false"
	)
	boolean hasNext,

	@Schema(
		description = "다음 커서 (마지막 송금 시간)",
		example = "2024-04-29T00:00:00",
		nullable = true
	)
	LocalDateTime nextCursorTime,

	@Schema(
		description = "다음 커서 ID (sendTime이 동일한 경우 식별 용도)",
		example = "456"
	)
	Long nextCursorId
) {}
