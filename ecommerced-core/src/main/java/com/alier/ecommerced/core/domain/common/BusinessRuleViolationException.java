package com.alier.ecommerced.core.domain.common;

/**
 * Exception thrown when a business rule is violated in the domain.
 * This represents a domain-specific error that should be handled by the application layer.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
} 