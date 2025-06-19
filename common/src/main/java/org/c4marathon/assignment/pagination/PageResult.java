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

	public static <T> PageResult<T> of(List<T> contents, int pageNumber, int pageSize, long totalElements) {
		Objects.requireNonNull(contents, "contents must not be null");
		int totalPages = (int) Math.ceil((double) totalElements / pageSize);
		return new PageResult<>(contents, pageNumber, pageSize, totalElements, totalPages);
	}

	public static <T> PageResult<T> empty(int pageNumber, int pageSize) {
		return new PageResult<>(Collections.emptyList(), pageNumber, pageSize, 0, 0);
	}
	
	public boolean hasNext() {
		return pageNumber + 1 < totalPages;
	}

	public boolean hasPrevious() {
		return pageNumber > 0;
	}
}
