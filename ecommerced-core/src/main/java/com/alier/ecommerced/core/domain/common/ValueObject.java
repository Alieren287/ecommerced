package com.alier.ecommerced.core.domain.common;

import lombok.EqualsAndHashCode;

/**
 * Base class for all value objects in the hexagonal architecture.
 * Value objects are immutable objects that represent a descriptive aspect of the domain.
 * They are defined only by their attributes and should implement value equality.
 */
@EqualsAndHashCode
public abstract class ValueObject {

    /**
     * Validates the value object's state.
     * Should be called in constructors to ensure invariants are maintained.
     */
    protected abstract void validate();

    /**
     * Template method for validation with a custom error message.
     *
     * @param condition the condition that must be true
     * @param message   the error message if condition is false
     */
    protected void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Template method for validation with a custom error message and exception type.
     *
     * @param condition the condition that must be true
     * @param exception the exception to throw if condition is false
     */
    protected void require(boolean condition, RuntimeException exception) {
        if (!condition) {
            throw exception;
        }
    }
} 