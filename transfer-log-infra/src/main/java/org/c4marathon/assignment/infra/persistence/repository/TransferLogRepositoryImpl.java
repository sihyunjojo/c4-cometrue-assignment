package org.c4marathon.assignment.infra.persistence.repository;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.model.TransferLog;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.repository.TransferLogRepository;
import org.c4marathon.assignment.infra.persistence.repository.jpa.JpaTransferLogRepository;
import org.c4marathon.assignment.infra.persistence.repository.query.TransferLogQueryRepository;
import org.c4marathon.assignment.pagination.PageRequest;
import org.c4marathon.assignment.pagination.PageResult;
import org.c4marathon.assignment.pagination.SliceResult;
import org.c4marathon.assignment.pagination.converter.PageConverter;
import org.c4marathon.assignment.pagination.converter.SliceConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransferLogRepositoryImpl implements TransferLogRepository {

	private final JpaTransferLogRepository jpa;
	private final TransferLogQueryRepository query;

	@Override
	public void save(TransferLog transferLog) {
		jpa.save(transferLog);
	}

	@Override
	public SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAndIdAfterCursor(String accountNumber,
		LocalDateTime cursorTime, Long cursorId, int size) {
		Slice<TransferLog> allByAccountNumberAndSendTimeAndIdAfterCursor = query.findAllByAccountNumberAndSendTimeAndIdAfterCursor(
			accountNumber, cursorTime, cursorId, size);

		return SliceConverter.fromSpringSlice(allByAccountNumberAndSendTimeAndIdAfterCursor);
	}

	@Override
	public SliceResult<TransferLog> findAllByAccountNumberAndSendTimeAfterCursor(String accountNumber,
		LocalDateTime cursorTime, int size) {
		Slice<TransferLog> allByAccountNumberAndSendTimeAfterCursor = query.findAllByAccountNumberAndSendTimeAfterCursor(
			accountNumber, cursorTime, size);

		return SliceConverter.fromSpringSlice(allByAccountNumberAndSendTimeAfterCursor);
	}

	@Override
	public PageResult<TransferLog> findPageByAccountNumber(String accountNumber, PageRequest request) {
		Pageable springPageable =
			org.springframework.data.domain.PageRequest.of(
				request.getPage(),
				request.getSize(),
				Sort.by("sendTime").descending().and(Sort.by("id").descending())
			);

		Page<TransferLog> result = query.findPageByAccountNumber(accountNumber, springPageable);

		return PageConverter.fromSpringPage(result);
	}

}
