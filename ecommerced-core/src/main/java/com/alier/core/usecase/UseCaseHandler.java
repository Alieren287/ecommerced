package com.alier.core.usecase;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for executing use cases with additional features like logging, validation, etc.
 */
public class UseCaseHandler {

    /**
     * Executes a use case synchronously
     *
     * @param useCase  the use case to execute
     * @param input    the input for the use case
     * @param <INPUT>  the input type
     * @param <OUTPUT> the output type
     * @return the result of the use case execution
     */
    public <INPUT, OUTPUT> OUTPUT execute(UseCase<INPUT, OUTPUT> useCase, INPUT input) {
        try {
            // Here you can add cross-cutting concerns like:
            // - Logging
            // - Validation
            // - Transaction management
            // - Security checks
            // - Performance monitoring

            return useCase.execute(input);

        } catch (Exception e) {
            // Log the error and re-throw
            throw e;
        }
    }

    /**
     * Executes a use case asynchronously
     *
     * @param useCase  the use case to execute
     * @param input    the input for the use case
     * @param <INPUT>  the input type
     * @param <OUTPUT> the output type
     * @return a CompletableFuture containing the result
     */
    public <INPUT, OUTPUT> CompletableFuture<OUTPUT> executeAsync(UseCase<INPUT, OUTPUT> useCase, INPUT input) {
        return CompletableFuture.supplyAsync(() -> execute(useCase, input));
    }
} 