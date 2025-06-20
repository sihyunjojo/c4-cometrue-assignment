package org.c4marathon.assignment.domain.service;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member registerMemer(Member member) {
		return memberRepository.save(member);
	}

}
