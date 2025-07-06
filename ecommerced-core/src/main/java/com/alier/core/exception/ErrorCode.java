package com.alier.core.exception;

/**
 * Interface for error codes that provides code and message for exceptions
 */
public interface ErrorCode {

    /**
     * Gets the error code identifier
     *
     * @return error code as string
     */
    String getCode();

    /**
     * Gets the error message
     *
     * @return error message as string
     */
    String getMessage();
} 