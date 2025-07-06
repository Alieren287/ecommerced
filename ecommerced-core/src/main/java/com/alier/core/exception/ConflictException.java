package com.alier.core.exception;

/**
 * Exception thrown when there's a conflict in the business logic
 */
public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public ConflictException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
} 