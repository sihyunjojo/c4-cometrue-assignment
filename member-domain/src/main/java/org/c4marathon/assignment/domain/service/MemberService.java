package org.c4marathon.assignment.domain.service;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member registerember(Member member) {
		memberRepository.save(member);
		return member;
	}

}
