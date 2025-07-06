package com.alier.core.exception;

import lombok.Getter;

/**
 * Base exception class containing common fields and methods for all application exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * The error code containing code, message, and HTTP status
     */
    private final ErrorCode errorCode;

    /**
     * Optional arguments for parameterized error messages
     * Used for dynamic content in error messages like "User with ID {0} not found"
     */
    private final Object[] args;

    /**
     * Constructor with error code and optional arguments
     *
     * @param errorCode the error code
     * @param args      optional arguments for parameterized messages
     */
    protected BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * Constructor with error code, cause, and optional arguments
     *
     * @param errorCode the error code
     * @param cause     the root cause
     * @param args      optional arguments for parameterized messages
     */
    protected BaseException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * Gets the error code identifier
     *
     * @return error code as string
     */
    public String getCode() {
        return errorCode.getCode();
    }
} 