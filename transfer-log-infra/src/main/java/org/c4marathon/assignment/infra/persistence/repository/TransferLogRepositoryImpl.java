package org.c4marathon.assignment.infra.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.infra.persistence.entity.TransferLogJpaEntity;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.repository.TransferLogRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaTransferLogRepository;
import org.c4marathon.assignment.infra.persistence.repository.query.TransferLogQueryRepository;
import org.c4marathon.assignment.pagination.PageRequest;
import org.c4marathon.assignment.pagination.PageResult;
import org.c4marathon.assignment.pagination.SliceResult;
import org.c4marathon.assignment.pagination.converter.PageConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Repository
@RequiredArgsConstructor
public class TransferLogRepositoryImpl implements TransferLogRepository {

	private final JpaTransferLogRepository jpaTransferLogRepository;
	private final TransferLogQueryRepository query;

	@Override
	public void save(TransferLog transferLog) {
		TransferLogJpaEntity jpaEntity = TransferLogJpaEntity.fromDomain(transferLog);
		jpaTransferLogRepository.save(jpaEntity);
	}

	@Override
	public SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(
		String accountNumber, LocalDateTime cursorTime, Long cursorId, int size) {
		Slice<TransferLogJpaEntity> jpaSlice = query.findAllByAccountNumberAndSendTimeAndIdAfterCursor(
			accountNumber, cursorTime, cursorId, size);

		List<TransferLog> domainList = jpaSlice.getContent().stream()
			.map(TransferLogJpaEntity::toDomain)
			.toList();

		return SliceResult.of(
			domainList,
			jpaSlice.hasNext()
		);
	}

	@Override
	public SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(
		String accountNumber, LocalDateTime cursorTime, int size) {
		Slice<TransferLogJpaEntity> jpaSlice = query.findAllByAccountNumberAndSendTimeAfterCursor(
			accountNumber, cursorTime, size);

		List<TransferLog> domainList = jpaSlice.getContent().stream()
			.map(TransferLogJpaEntity::toDomain)
			.toList();

		return SliceResult.of(
			domainList,
			jpaSlice.hasNext()
		);
	}

	@Override
	public PageResult<TransferLog> findPageByAccountNumber(String accountNumber, PageRequest request) {
		Pageable springPageable =
			org.springframework.data.domain.PageRequest.of(
				request.getPage(),
				request.getSize(),
				Sort.by("sendTime").descending().and(Sort.by("id").descending())
			);

		Page<TransferLogJpaEntity> result = query.findPageByAccountNumber(accountNumber, springPageable);

		// Convert Page<TransferLogJpaEntity> to Page<TransferLog> before converting to PageResult
		List<TransferLog> content = result.getContent().stream()
			.map(TransferLogJpaEntity::toDomain)
			.toList();

		// Create a new Page with the converted content
		Page<TransferLog> domainPage = new PageImpl<>(
			content,
			result.getPageable(),
			result.getTotalElements()
		);

		return PageConverter.fromSpringPage(domainPage);
	}

}
