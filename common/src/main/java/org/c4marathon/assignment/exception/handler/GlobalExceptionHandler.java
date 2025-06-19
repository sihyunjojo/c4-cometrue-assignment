package org.c4marathon.assignment.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

import org.c4marathon.assignment.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
		StringBuilder sb = new StringBuilder();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			sb.append(error.getField())
				.append(": ")
				.append(error.getDefaultMessage())
				.append("; ");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.res(400, sb.toString()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
		log.error("자원 미존재 예외 발생: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.res(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("잘못된 요청 파라미터: {}", ex.getMessage(), ex); // 📌 파라미터 오류
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.res(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
		log.warn("호출 시점의 상태 오류: {}", ex.getMessage(), ex); // 📌 상태 오류
		return ResponseEntity.status(HttpStatus.CONFLICT) // 보통 상태 불일치는 409로도 응답 가능
			.body(ApiResponse.res(HttpStatus.CONFLICT.value(), ex.getMessage()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
		return ResponseEntity.status(ex.getStatusCode())
			.body(ApiResponse.res(ex.getStatusCode().value(), ex.getReason()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		log.error("예외 발생: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.res(500, ex.getMessage()));
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ApiResponse<Void>> handleOptimisticLockException(OptimisticLockException ex) {
		log.error("동시성 충돌 예외 발생: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ApiResponse.res(409, "현재 다른 요청에 의해 처리 중입니다. 잠시 후 다시 시도해주세요."));
	}
}
