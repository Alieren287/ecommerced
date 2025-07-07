package com.alier.core.usecase;

import com.alier.core.logging.LoggerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Handler for executing use cases with additional features like logging, validation, etc.
 * This handler provides cross-cutting concerns and integrates with the centralized logging system.
 */
@Slf4j
public class UseCaseHandler {

    private final Executor executor;

    public UseCaseHandler() {
        this.executor = null; // Uses ForkJoinPool.commonPool() by default
    }

    public UseCaseHandler(Executor executor) {
        this.executor = executor;
    }

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
        String useCaseName = useCase.getClass().getSimpleName();
        long startTime = System.currentTimeMillis();

        log.info("Starting useCase execution: {}", useCaseName);
        log.debug("UseCase input: {}", input);

        try {
            // Execute the use case
            OUTPUT result = useCase.execute(input);

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("UseCase completed successfully: {} in {}ms", useCaseName, executionTime);
            log.debug("UseCase output: {}", result);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("UseCase execution failed: {} after {}ms - {}", useCaseName, executionTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Executes a use case asynchronously
     * Note: This method copies the current thread's logging context to the async thread
     *
     * @param useCase  the use case to execute
     * @param input    the input for the use case
     * @param <INPUT>  the input type
     * @param <OUTPUT> the output type
     * @return a CompletableFuture containing the result
     */
    public <INPUT, OUTPUT> CompletableFuture<OUTPUT> executeAsync(UseCase<INPUT, OUTPUT> useCase, INPUT input) {
        String useCaseName = useCase.getClass().getSimpleName();

        // Capture current logging context
        LoggerUtil.LoggingContext currentContext = LoggerUtil.copyContext();

        log.info("Starting async useCase execution: {}", useCaseName);

        CompletableFuture<OUTPUT> future = executor != null ?
                CompletableFuture.supplyAsync(() -> executeWithContext(useCase, input, currentContext), executor) :
                CompletableFuture.supplyAsync(() -> executeWithContext(useCase, input, currentContext));

        return future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Async useCase execution failed: {} - {}", useCaseName, throwable.getMessage());
            } else {
                log.info("Async useCase completed successfully: {}", useCaseName);
            }
        });
    }

    /**
     * Executes a use case with the provided logging context
     * This ensures proper logging context propagation in async scenarios
     */
    private <INPUT, OUTPUT> OUTPUT executeWithContext(UseCase<INPUT, OUTPUT> useCase, INPUT input, LoggerUtil.LoggingContext context) {
        try {
            // Apply the logging context to the current thread
            LoggerUtil.applyContext(context);

            // Execute the use case
            return execute(useCase, input);

        } finally {
            // Clean up the context after execution
            LoggerUtil.clearContext();
        }
    }
} 