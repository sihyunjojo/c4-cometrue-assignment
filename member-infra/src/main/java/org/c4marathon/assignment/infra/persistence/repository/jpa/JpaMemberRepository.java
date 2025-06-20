package org.c4marathon.assignment.infra.persistence.repository.jpa;

import org.c4marathon.assignment.infra.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<MemberJpaEntity, Long> {
}
