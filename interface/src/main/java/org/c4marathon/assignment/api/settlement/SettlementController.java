package org.c4marathon.assignment.api.settlement;

import java.util.List;

import org.c4marathon.assignment.api.settlement.dto.SettlementRequestDto;
import org.c4marathon.assignment.api.settlement.dto.SettlementResponseDto;
import org.c4marathon.assignment.response.ApiResponse;
import org.c4marathon.assignment.usecase.settlement.SettlementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlements")
public class SettlementController {

	private final SettlementUseCase settlementUseCase;

	@Operation(summary = "정산 요청", description = "사용자들이 정산 요청하는 API입니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<List<SettlementResponseDto>>> settle(
		@RequestBody @Valid SettlementRequestDto request
	) {
		try {
			List<SettlementResponseDto> result = settlementUseCase.settle(request);
			return ResponseEntity.ok(ApiResponse.res(200, "정산 성공", result));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest()
				.body(ApiResponse.res(400, e.getMessage()));
		}
	}
}
