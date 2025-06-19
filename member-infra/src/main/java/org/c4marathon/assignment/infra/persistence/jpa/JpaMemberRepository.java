package org.c4marathon.assignment.infra.persistence.jpa;

import org.c4marathon.assignment.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
}
