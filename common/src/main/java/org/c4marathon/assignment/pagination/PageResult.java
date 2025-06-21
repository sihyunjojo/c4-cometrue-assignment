package org.c4marathon.assignment.pagination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResult<T> {

	private final List<T> contents;
	private final int pageNumber;
	private final int pageSize;
	private final long totalElements;
	private final int totalPages;

	public static <T> PageResult<T> of(List<T> contents, int pageNumber, int pageSize, long totalElements,
		int totalPages) {
		if (pageSize <= 0) {
			throw new IllegalArgumentException("페이지 사이즈는 0보다 커야합니다.");
		}
		return new PageResult<>(contents, pageNumber, pageSize, totalElements, totalPages);
	}

	public boolean hasNext() {
		return pageNumber + 1 < totalPages;
	}

	public boolean hasPrevious() {
		return pageNumber > 0;
	}
}
