package org.c4marathon.assignment.infra.persistence.repository.query;

import static org.c4marathon.assignment.infra.persistence.entity.QMainAccountJpaEntity.mainAccountJpaEntity;
import static org.c4marathon.assignment.infra.persistence.entity.QPendingTransferJpaEntity.pendingTransferJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.c4marathon.assignment.domain.model.Member;
import org.c4marathon.assignment.domain.model.PendingTransfer;
import org.c4marathon.assignment.enums.TransferStatus;
import org.c4marathon.assignment.infra.persistence.entity.PendingTransferJpaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PendingTransferQueryRepositoryImpl implements PendingTransferQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Map<Member, List<PendingTransfer>> findRemindTargetGroupedByMember(LocalDateTime remindTime) {
		// 1. 만료된 PendingTransfer 조회 (QueryDSL 사용)
		List<PendingTransferJpaEntity> pendingTransfers = queryFactory
			.selectFrom(pendingTransferJpaEntity)
			.join(pendingTransferJpaEntity.toMainAccount, mainAccountJpaEntity).fetchJoin()
			.join(mainAccountJpaEntity.member).fetchJoin()
			.where(
				pendingTransferJpaEntity.status.eq(TransferStatus.PENDING),
				pendingTransferJpaEntity.expiredAt.loe(remindTime)
			)
			.fetch();

		// 2. Member 기준으로 그룹화
		return pendingTransfers.stream()
			.collect(Collectors.groupingBy(
				pendingTransfer -> {
					return pendingTransfer.getToMainAccount().getMember().toDomain();
				},
				Collectors.mapping(PendingTransferJpaEntity::toDomain, Collectors.toList())
			));
	}
}
