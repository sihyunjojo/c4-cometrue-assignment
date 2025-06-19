package org.c4marathon.assignment.usecase.member;

import org.c4marathon.assignment.domain.model.MainAccount;
import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.service.MainAccountService;
import org.c4marathon.assignment.domain.service.MemberService;
import org.c4marathon.assignment.api.member.dto.MemberRegistrationRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberAccountUseCase {

	private final MemberService memberService;
	private final MainAccountService mainAccountService;

	@Transactional
	public MainAccount registerMemberWithAccount(MemberRegistrationRequestDto request) {
		Member member = Member.builder()
			.name(request.name())
			.email(request.email())
			.password(request.password())
			.build();

		member = memberService.registerember(member);
		return mainAccountService.createMainAccountForMember(member);
	}
}
