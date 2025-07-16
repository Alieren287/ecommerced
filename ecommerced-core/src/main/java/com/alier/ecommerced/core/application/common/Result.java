package com.alier.ecommerced.core.application.common;

import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Result wrapper for handling success/failure scenarios in the application layer.
 * Provides a functional approach to error handling without exceptions.
 */
@Getter
public class Result<T> {

    private final T value;
    private final String error;
    private final boolean success;

    private Result(T value, String error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    /**
     * Creates a successful result with a value.
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    /**
     * Creates a successful result without a value.
     */
    public static <T> Result<T> success() {
        return new Result<>(null, null, true);
    }

    /**
     * Creates a failed result with an error message.
     */
    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error, false);
    }

    /**
     * Creates a failed result from an exception.
     */
    public static <T> Result<T> failure(Exception exception) {
        return new Result<>(null, exception.getMessage(), false);
    }

    /**
     * Executes the given function if the result is successful.
     */
    public <U> Result<U> map(Function<T, U> mapper) {
        if (success) {
            try {
                return success(mapper.apply(value));
            } catch (Exception e) {
                return failure(e);
            }
        }
        return failure(error);
    }

    /**
     * Executes the given function if the result is successful, flattening nested Results.
     */
    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (success) {
            try {
                return mapper.apply(value);
            } catch (Exception e) {
                return failure(e);
            }
        }
        return failure(error);
    }

    /**
     * Executes the given consumer if the result is successful.
     */
    public Result<T> onSuccess(Consumer<T> consumer) {
        if (success) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Executes the given consumer if the result is failed.
     */
    public Result<T> onFailure(Consumer<String> consumer) {
        if (!success) {
            consumer.accept(error);
        }
        return this;
    }

    /**
     * Returns the value if successful, or the provided default value if failed.
     */
    public T getOrElse(T defaultValue) {
        return success ? value : defaultValue;
    }

    /**
     * Returns the value if successful, or throws an exception if failed.
     */
    public T getOrThrow() {
        if (success) {
            return value;
        }
        throw new RuntimeException(error);
    }

    public boolean isFailure() {
        return !success;
    }
}