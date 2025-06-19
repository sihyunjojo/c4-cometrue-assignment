package org.c4marathon.assignment.usecase.transferlog;

import org.c4marathon.assignment.domain.model.TransferLog;
import org.c4marathon.assignment.domain.service.TransferLogService;
import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.api.transferlog.dto.TransferLogCursorPageResponseDto;
import org.c4marathon.assignment.api.transferlog.dto.TransferLogDto;
import org.c4marathon.assignment.api.transferlog.dto.TransferLogSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// DTO 조립은 Application 계층(UseCase)에서 담당
// UseCase는 도메인 흐름에 집중
// 도메인 규칙/정책(비즈니스 정책) 적용 + 흐름 제어
@Service
@RequiredArgsConstructor
public class TransferLogUseCase {

	private final TransferLogService transferLogService;

	public TransferLogCursorPageResponseDto findAllBySendTimeAfterCursor(TransferLogSearchRequestDto request) {
		LocalDateTime startAt = Optional.ofNullable(request.cursorStartAt())
			.orElse(LocalDateTime.now().minusDays(10));
		String accountNumber = request.accountNumber();
		Long id = request.cursorId();
		int size = Optional.ofNullable(request.size()).orElse(10);

		Slice<TransferLog> slice = (id == null)
			? transferLogService.findAllBySendTimeAfterCursor(accountNumber, startAt, size)
			: transferLogService.findAllBySendTimeAndIdAfterCursor(accountNumber, startAt, id, size);

		return buildCursorPageResponse(slice.getContent(), slice.hasNext());
	}

	public TransferLogCursorPageResponseDto findAllByOffsetOrDefaultPaging(
		TransferLogSearchRequestDto request) {
		String accountNumber = request.accountNumber();
		int page = Optional.ofNullable(request.page()).orElse(0);
		int size = Optional.ofNullable(request.size()).orElse(10);

		Pageable pageable = createPageable(page, size, Sort.by("sendTime").descending().and(Sort.by("id").descending()));

		Page<TransferLog> pageResult = transferLogService.findRecentLogs(accountNumber, pageable);

		return buildCursorPageResponse(pageResult.getContent(), pageResult.hasNext());
	}

	// 페이지 및 정렬 생성 헬퍼
	private Pageable createPageable(int page, int size, Sort sort) {
		return PageRequest.of(page, size, sort);
	}

	// 커서 기반 응답 조립 헬퍼
	private TransferLogCursorPageResponseDto buildCursorPageResponse(List<TransferLog> logs, boolean hasNext) {
		List<TransferLogDto> dtos = logs.stream()
			.map(TransferLogDto::from)
			.toList();

		if (hasNext && !logs.isEmpty()) {
			TransferLog last = logs.get(logs.size() - 1);
			return TransferLogCursorPageResponseDto.builder()
				.data(dtos)
				.hasNext(true)
				.nextCursorTime(last.getSendTime())
				.nextCursorId(last.getId())
				.build();
		}
		return TransferLogCursorPageResponseDto.builder()
			.data(dtos)
			.hasNext(false)
			.nextCursorTime(null)
			.nextCursorId(null)
			.build();
	}

}
