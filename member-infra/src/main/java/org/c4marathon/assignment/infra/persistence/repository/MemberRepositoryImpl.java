package org.c4marathon.assignment.infra.persistence.repository;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.repository.MemberRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaMemberRepository;
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
