package org.c4marathon.assignment.infra.persistence.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.model.QTransferLog;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class TransferLogQueryRepositoryImpl implements TransferLogQueryRepository {

	private final JPAQueryFactory queryFactory;

	// @Override
	// public Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
	// 	String accountNumber,
	// 	LocalDateTime cursorTime,
	// 	Long cursorId,
	// 	int size
	// ) {
	// 	BooleanExpression accountMatch = buildAccountMatch(accountNumber);
	// 	DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);
	//
	// 	BooleanExpression cursorCondition = sortTime.gt(cursorTime)
	// 		.or(sortTime.eq(cursorTime).and(transferLog.id.gt(cursorId)));
	//
	// 	return fetchSlice(accountMatch.and(cursorCondition), sortTime.asc(), size);
	// }

	@Override
	public Slice<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
		String accountNumber,
		LocalDateTime cursorTime,
		Long cursorId,
		int size
	) {
		List<TransferLog> fromLogs = queryFactory
			.selectFrom(QTransferLog.transferLog)
			.where(
				QTransferLog.transferLog.from.number.eq(accountNumber)
					.and(
						QTransferLog.transferLog.sendTime.gt(cursorTime)
							.or(
								QTransferLog.transferLog.sendTime.eq(cursorTime)
									.and(QTransferLog.transferLog.id.gt(cursorId))
							)
					)
			)
			.orderBy(QTransferLog.transferLog.sendTime.asc(), QTransferLog.transferLog.id.asc())
			.limit(size + 1)
			.fetch();

		List<TransferLog> toLogs = queryFactory
			.selectFrom(QTransferLog.transferLog)
			.where(
				QTransferLog.transferLog.to.number.eq(accountNumber)
					.and(
						QTransferLog.transferLog.receiverTime.gt(cursorTime)
							.or(
								QTransferLog.transferLog.receiverTime.eq(cursorTime)
									.and(QTransferLog.transferLog.id.gt(cursorId))
							)
					)
			)
			.orderBy(QTransferLog.transferLog.receiverTime.asc(), QTransferLog.transferLog.id.asc())
			.limit(size + 1)
			.fetch();

		List<TransferLog> merged = Stream.concat(fromLogs.stream(), toLogs.stream())
			.sorted(Comparator
				.comparing((TransferLog log) ->
					accountNumber.equals(log.getFrom().getNumber()) ? log.getSendTime() : log.getReceiverTime()
				)
				.thenComparing(TransferLog::getId)
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
	public Slice<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(
		String accountNumber,
		LocalDateTime cursorTime,
		int size 	) {
		BooleanExpression accountMatch = buildAccountMatch(accountNumber);
		DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);

		BooleanExpression cursorCondition = sortTime.goe(cursorTime);

		return fetchSlice(accountMatch.and(cursorCondition), sortTime.asc(), size);
	}

	@Override
	public Page<TransferLog> findPageByAccountNumber(
		String accountNumber,
		Pageable pageable
	) {
		BooleanExpression accountMatch = buildAccountMatch(accountNumber);
		DateTimeExpression<LocalDateTime> sortTime = buildSortTime(accountNumber);

		List<TransferLog> results = queryFactory
			.selectFrom(QTransferLog.transferLog)
			.where(accountMatch)
			.orderBy(sortTime.desc(), QTransferLog.transferLog.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(
			queryFactory
				.select(QTransferLog.transferLog.count())
				.from(QTransferLog.transferLog)
				.where(accountMatch)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(results, pageable, total);
	}


	private BooleanExpression buildAccountMatch(String accountNumber) {
		return QTransferLog.transferLog.from.number.eq(accountNumber)
			.or(QTransferLog.transferLog.to.number.eq(accountNumber));
	}

	private DateTimeExpression<LocalDateTime> buildSortTime(String accountNumber) {
		return new CaseBuilder()
			.when(QTransferLog.transferLog.from.number.eq(accountNumber)).then(QTransferLog.transferLog.sendTime)
			.when(QTransferLog.transferLog.to.number.eq(accountNumber)).then(QTransferLog.transferLog.receiverTime)
			.otherwise(QTransferLog.transferLog.sendTime);
	}

	private Slice<TransferLog> fetchSlice(
		BooleanExpression condition,
		OrderSpecifier<?> sortOrder,
		int size
	) {
		List<TransferLog> results = queryFactory
			.selectFrom(QTransferLog.transferLog)
			.where(condition)
			.orderBy(sortOrder, QTransferLog.transferLog.id.asc())
			.limit(size + 1L)
			.fetch();

		boolean hasNext = results.size() > size;
		if (hasNext) {
			results.remove(results.size() - 1);
		}
return new SliceImpl<>(results, PageRequest.of(0, size), hasNext);
	}

}
