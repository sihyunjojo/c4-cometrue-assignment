package org.c4marathon.assignment.infra.persistence.repository.jpa;

import org.c4marathon.assignment.infra.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemberRepository extends JpaRepository<MemberJpaEntity, Long> {
}
