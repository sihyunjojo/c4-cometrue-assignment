package org.c4marathon.assignment.pagination.converter;

import org.c4marathon.assignment.pagination.SliceResult;
import org.springframework.data.domain.Slice;

public class SliceConverter {

	private SliceConverter() {
		// 유틸리티 클래스는 인스턴스화하지 않음
	}

	public static <T> SliceResult<T> fromSpringSlice(Slice<T> slice) {
		if (slice == null || slice.getContent() == null) {
			return SliceResult.empty();
		}
		return SliceResult.of(slice.getContent(), slice.hasNext());
	}
}
