package org.c4marathon.assignment.domain.repository;

import org.c4marathon.assignment.domain.model.Member;

public interface MemberRepository {
	Member save(Member member);
}
