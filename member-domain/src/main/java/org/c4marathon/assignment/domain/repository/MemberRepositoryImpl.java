package org.c4marathon.assignment.domain.repository;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.infra.persistence.jpa.JpaMemberRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final JpaMemberRepository jpa;

	@Override
	public Member save(Member member) {
		return jpa.save(member);
	}
}
