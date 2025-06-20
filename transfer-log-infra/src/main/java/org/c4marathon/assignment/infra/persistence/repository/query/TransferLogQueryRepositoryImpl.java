package org.c4marathon.assignment.infra.persistence.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.infra.persistence.entity.TransferLogJpaEntity;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.c4marathon.assignment.infra.persistence.entity.QTransferLogJpaEntity.transferLogJpaEntity;

@Repository
@RequiredArgsConstructor
public class TransferLogQueryRepositoryImpl implements TransferLogQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<TransferLogJpaEntity> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
            String accountNumber,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {
        List<TransferLogJpaEntity> fromLogs = queryFactory
            .selectFrom(transferLogJpaEntity)
            .where(
                transferLogJpaEntity.from.number.eq(accountNumber)
                    .and(
                        transferLogJpaEntity.sendTime.gt(cursorTime)
                            .or(
                                transferLogJpaEntity.sendTime.eq(cursorTime)
                                    .and(transferLogJpaEntity.id.gt(cursorId))
                            )
                    )
            )
            .orderBy(transferLogJpaEntity.sendTime.asc(), transferLogJpaEntity.id.asc())
            .limit(size + 1)
            .fetch();

        List<TransferLogJpaEntity> toLogs = queryFactory
            .selectFrom(transferLogJpaEntity)
            .where(
                transferLogJpaEntity.to.number.eq(accountNumber)
                    .and(
                        transferLogJpaEntity.receiverTime.gt(cursorTime)
                            .or(
                                transferLogJpaEntity.receiverTime.eq(cursorTime)
                                    .and(transferLogJpaEntity.id.gt(cursorId))
                            )
                    )
            )
            .orderBy(transferLogJpaEntity.receiverTime.asc(), transferLogJpaEntity.id.asc())
            .limit(size + 1)
            .fetch();

        List<TransferLogJpaEntity> merged = Stream.concat(fromLogs.stream(), toLogs.stream())
            .sorted(Comparator
                .comparing((TransferLogJpaEntity log) ->
                    accountNumber.equals(log.getFrom().getNumber()) ? log.getSendTime() : log.getReceiverTime()
                )
                .thenComparing(TransferLogJpaEntity::getId)
            )
            .limit(size + 1)
            .toList();

        boolean hasNext = merged.size() > size;
        if (hasNext) {
            merged = merged.subList(0, size);
        }

        return new SliceImpl<>(merged, PageRequest.of(0, size), hasNext);
    }

    @Override
    public Slice<TransferLogJpaEntity> findAllByAccountNumberAndSendTimeAfterCursor(
            String accountNumber,
            LocalDateTime cursorTime,
            int size
    ) {
        BooleanExpression accountMatch = buildAccountMatch(accountNumber);
        DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);

        BooleanExpression cursorCondition = sortTime.goe(cursorTime);

        return fetchSlice(accountMatch.and(cursorCondition), sortTime.asc(), size);
    }

    @Override
    public Page<TransferLogJpaEntity> findPageByAccountNumber(
            String accountNumber,
            Pageable pageable
    ) {
        BooleanExpression accountMatch = buildAccountMatch(accountNumber);
        DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);

        List<TransferLogJpaEntity> results = queryFactory
            .selectFrom(transferLogJpaEntity)
            .where(accountMatch)
            .orderBy(sortTime.desc(), transferLogJpaEntity.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = Optional.ofNullable(
            queryFactory
                .select(transferLogJpaEntity.count())
                .from(transferLogJpaEntity)
                .where(accountMatch)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression buildAccountMatch(String accountNumber) {
        return transferLogJpaEntity.from.number.eq(accountNumber)
            .or(transferLogJpaEntity.to.number.eq(accountNumber));
    }

    private DateTimeExpression<LocalDateTime> buildSortTime(String accountNumber) {
        return new CaseBuilder()
            .when(transferLogJpaEntity.from.number.eq(accountNumber)).then(transferLogJpaEntity.sendTime)
            .when(transferLogJpaEntity.to.number.eq(accountNumber)).then(transferLogJpaEntity.receiverTime)
            .otherwise(transferLogJpaEntity.sendTime);
    }

    private Slice<TransferLogJpaEntity> fetchSlice(
            BooleanExpression condition,
            OrderSpecifier<?> sortOrder,
            int size
    ) {
        List<TransferLogJpaEntity> results = queryFactory
            .selectFrom(transferLogJpaEntity)
            .where(condition)
            .orderBy(sortOrder, transferLogJpaEntity.id.asc())
            .limit(size + 1L)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, PageRequest.of(0, size), hasNext);
    }
}
// 		String accountNumber,
// 		LocalDateTime cursorTime,
// 		int size 	) {
// 		BooleanExpression accountMatch = buildAccountMatch(accountNumber);
// 		DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);
//
// 		BooleanExpression cursorCondition = sortTime.goe(cursorTime);
//
// 		return fetchSlice(accountMatch.and(cursorCondition), sortTime.asc(), size);
// 	}
//
// 	@Override
// 	public Page<TransferLog> findPageByAccountNumber(
// 		String accountNumber,
// 		Pageable pageable
// 	) {
// 		BooleanExpression accountMatch = buildAccountMatch(accountNumber);
// 		DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);
//
// 		List<TransferLog> results = queryFactory
// 			.selectFrom(QTransferLog.transferLog)
// 			.where(accountMatch)
// 			.orderBy(sortTime.desc(), QTransferLog.transferLog.id.desc())
// 			.offset(pageable.getOffset())
// 			.limit(pageable.getPageSize())
// 			.fetch();
//
// 		Long total = Optional.ofNullable(
// 			queryFactory
// 				.select(QTransferLog.transferLog.count())
// 				.from(QTransferLog.transferLog)
// 				.where(accountMatch)
// 				.fetchOne()
// 		).orElse(0L);
//
// 		return new PageImpl<>(results, pageable, total);
// 	}
//
//
// 	private BooleanExpression buildAccountMatch(String accountNumber) {
// 		return QTransferLog.transferLog.from.number.eq(accountNumber)
// 			.or(QTransferLog.transferLog.to.number.eq(accountNumber));
// 	}
//
// 	private DateTimeExpression<LocalDateTime> buildSortTime(String accountNumber) {
// 		return new CaseBuilder()
// 			.when(QTransferLog.transferLog.from.number.eq(accountNumber)).then(QTransferLog.transferLog.sendTime)
// 			.when(QTransferLog.transferLog.to.number.eq(accountNumber)).then(QTransferLog.transferLog.receiverTime)
// 			.otherwise(QTransferLog.transferLog.sendTime);
// 	}
//
// 	private Slice<TransferLog> fetchSlice(
// 		BooleanExpression condition,
// 		OrderSpecifier<?> sortOrder,
// 		int size
// 	) {
// 		List<TransferLog> results = queryFactory
// 			.selectFrom(QTransferLog.transferLog)
// 			.where(condition)
// 			.orderBy(sortOrder, QTransferLog.transferLog.id.asc())
// 			.limit(size + 1L)
// 			.fetch();
//
// 		boolean hasNext = results.size() > size;
// 		if (hasNext) {
// 			results.remove(results.size() - 1);
// 		}
// return new SliceImpl<>(results, PageRequest.of(0, size), hasNext);
// 	}
//
// }
