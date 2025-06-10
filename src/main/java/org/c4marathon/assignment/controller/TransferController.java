package org.c4marathon.assignment.controller;

import org.c4marathon.assignment.common.response.ApiResponse;
import org.c4marathon.assignment.dto.transfer.AccountNumberTransferRequestDto;
import org.c4marathon.assignment.dto.transfer.TransferRequestDto;
import org.c4marathon.assignment.usecase.TransferUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// fixme: 멀티 모듈 및 핵사고날 아키택처 / 코틀린으로 추가
// 클린 아키텍처: UseCase는 별도의 서비스로 분리
// 송금은 MainAccount 자체의 책임이 아님
// → 송금은 계좌의 서브기능이 아니라, 고유한 도메인 행위이기 때문에 TransferController로 분리하는 게 클린하고 확장 가능성도 좋다.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/transfer")
public class TransferController {

	private final TransferUseCase transferUseCase;

	@Operation(summary = "메인 계좌에서 메인 계좌로 즉시 송금 요청", description = "보류 없이 바로 송금하는 API입니다.")
	@PostMapping("main-to-main/2")
	public ResponseEntity<ApiResponse<String>> transferFromMainToMain(
		@Valid @RequestBody TransferRequestDto request
	) {
		transferUseCase.transfer(request);
		return ResponseEntity.ok(ApiResponse.res(200, "송금 성공"));
	}

	@Operation(summary = "메인 계좌에서 메인 계좌로 계좌번호로 즉시 송금 요청", description = "계좌번호를 사용하여 보류 없이 바로 송금하는 API입니다.")
	@PostMapping("main-to-main")
	public ResponseEntity<ApiResponse<String>> transferFromMainToMainByAccountNumber(
		@Valid @RequestBody AccountNumberTransferRequestDto request
	) {
		transferUseCase.transferByAccountNumber(request);
		return ResponseEntity.ok(ApiResponse.res(200, "송금 성공"));
	}
	
	@Operation(summary = "메인 계좌에서 적금 계좌로 계좌번호로 즉시 송금 요청", description = "계좌번호를 사용하여 메인 계좌에서 적금 계좌로 보류 없이 바로 송금하는 API입니다.")
	@PostMapping("/main-to-saving")
	public ResponseEntity<ApiResponse<String>> transferFromMainToSavingByAccountNumber(
		@Valid @RequestBody AccountNumberTransferRequestDto request
	) {
		transferUseCase.transferFromMainToSavingByAccountNumber(request);
		return ResponseEntity.ok(ApiResponse.res(200, "적금 계좌로 송금 성공"));
	}
}
