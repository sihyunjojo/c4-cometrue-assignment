package org.c4marathon.assignment.pagination.converter;

import org.c4marathon.assignment.pagination.PageRequest;
import org.c4marathon.assignment.pagination.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PageConverter {

	private PageConverter() {
		// 유틸리티 클래스이므로 인스턴스화 방지
	}

	public static PageRequest toPageRequest(Pageable pageable) {
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
	}

	public static <T> PageResult<T> fromSpringPage(Page<T> page) {
		return PageResult.of(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements()
		);
	}
}
