package com.alier.core.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends BaseException {

    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public ValidationException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
} 