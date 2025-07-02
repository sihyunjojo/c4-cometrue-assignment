package org.c4marathon.assignment.exception;

/**
 * 재시도가 가능한 예외를 나타냅니다.
 */
public class RetryableException extends RuntimeException {

	public RetryableException(String message) {
		super(message);
	}

	public RetryableException(String message, Throwable cause) {
		super(message, cause);
	}
}
