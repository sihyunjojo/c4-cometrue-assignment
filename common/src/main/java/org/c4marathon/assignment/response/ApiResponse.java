package org.c4marathon.assignment.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // Jackson이 JSON 변환 시, getter가 없는 필드를 직렬화하지 않음.
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // NON_NULL 옵션을 사용하면 JSON 변환 시 null 값을 가진 필드를 제외합니다.
public class ApiResponse<T> {
	private Integer code;
	private String message;
	private T result;

	public static <T> ApiResponse<T> res(final int code, final String message) {
		return new ApiResponse<>(code, message, null);
	}

	public static <T> ApiResponse<T> res(final int code, final String message, final T t) {
		return new ApiResponse<>(code, message, t);
	}
}
