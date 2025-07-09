package com.alier.core.usecase;

import com.alier.core.logging.LoggerUtil;
import org.slf4j.Logger;

/**
 * Handler for executing use cases with additional features like logging, validation, etc.
 * This handler provides cross-cutting concerns and integrates with the centralized logging system.
 *
 * Use the @UseCaseName annotation to define the business purpose and module of this handler.
 */
public class UseCaseHandler {

    private static final Logger logger = LoggerUtil.getLogger(UseCaseHandler.class);
    private static final Logger auditLogger = LoggerUtil.getAuditLogger();
    private static final Logger performanceLogger = LoggerUtil.getPerformanceLogger();

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
        String useCaseName = getUseCaseName();
        long startTime = System.currentTimeMillis();

        // General log: Technical execution details
        logger.info("Starting useCase execution: {}", useCaseName);
        logger.debug("UseCase input: {}", input);

        try {
            // Execute the use case
            OUTPUT result = useCase.execute(input);

            long executionTime = System.currentTimeMillis() - startTime;

            // Performance log: Execution metrics
            performanceLogger.info("UseCase performance: {} completed in {}ms", useCaseName, executionTime);

            // Audit log: Business event completion (only if user context is available)
            String userId = LoggerUtil.getUserId();
            if (userId != null && !userId.isEmpty()) {
                auditLogger.info("UseCase completed by user {} - {}", userId, useCaseName);
            }

            // General log: Technical completion details
            logger.info("UseCase completed successfully: {} in {}ms", useCaseName, executionTime);
            logger.debug("UseCase output: {}", result);
            
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            // Performance log: Execution metrics even on failure
            performanceLogger.warn("UseCase performance: {} failed after {}ms", useCaseName, executionTime);

            // Audit log: Business event failure (only if user context is available)
            String userId = LoggerUtil.getUserId();
            if (userId != null && !userId.isEmpty()) {
                auditLogger.error("UseCase failed for user {} - {} - {}", userId, useCaseName, e.getMessage());
            }

            // General log: Technical error details with full context
            logger.error("UseCase execution failed: {} after {}ms - {}", useCaseName, executionTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets the display name for this use case handler
     * Uses the @UseCaseName annotation if present, otherwise falls back to class name
     */
    private String getUseCaseName() {
        UseCaseName annotation = this.getClass().getAnnotation(UseCaseName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return this.getClass().getSimpleName();
    }
} 