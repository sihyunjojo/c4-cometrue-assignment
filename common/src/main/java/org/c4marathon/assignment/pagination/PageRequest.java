package org.c4marathon.assignment.pagination;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageRequest {

	private final int page;
	private final int size;

	public PageRequest(int page, int size) {
		if (page < 0) throw new IllegalArgumentException("page must be >= 0");
		if (size <= 0) throw new IllegalArgumentException("size must be > 0");
		this.page = page;
		this.size = size;
	}

	public static PageRequest of(int page, int size) {
		return new PageRequest(page, size);
	}
}
