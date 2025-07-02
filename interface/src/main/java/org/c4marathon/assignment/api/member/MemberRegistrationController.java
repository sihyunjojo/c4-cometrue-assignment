package org.c4marathon.assignment.api.member;

import org.c4marathon.assignment.api.member.dto.MainAccountResponseDto;
import org.c4marathon.assignment.api.member.dto.MemberRegistrationRequestDto;
import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.response.ApiResponse;
import org.c4marathon.assignment.usecase.member.MemberAccountUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberRegistrationController {

	private final MemberAccountUseCase memberAccountUseCase;

	@Operation(summary = "사용자 추가", description = "사용자 추가 및 사용자 메인 계좌 생성하는 API입니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<MainAccountResponseDto>> registerMemberWithAccount(
		@RequestBody MemberRegistrationRequestDto request) {
		MainAccount mainAccount = memberAccountUseCase.registerMemberWithAccount(request);
		MainAccountResponseDto mainAccountResponseDto = MainAccountResponseDto.from(mainAccount);
		return ResponseEntity.ok(
			ApiResponse.res(201, "회원 등록 및 메인 계좌 생성 완료", mainAccountResponseDto));
	}
}
