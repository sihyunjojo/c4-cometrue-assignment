package org.c4marathon.assignment.pagination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SliceResult<T> {

	private final List<T> contents;
	private final boolean hasNext;

	public static <T> SliceResult<T> of(List<T> contents, boolean hasNext) {
		Objects.requireNonNull(contents, "contents must not be null");
		return new SliceResult<>(contents, hasNext);
	}

	public static <T> SliceResult<T> empty() {
		return new SliceResult<>(Collections.emptyList(), false);
	}
}
