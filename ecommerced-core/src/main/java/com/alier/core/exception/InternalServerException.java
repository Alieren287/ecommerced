package com.alier.core.exception;

/**
 * Exception thrown when an internal server error occurs
 */
public class InternalServerException extends BaseException {

    public InternalServerException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public InternalServerException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
} 