package com.alier.core.logging;

import lombok.Getter;
import org.slf4j.Logger;

/**
 * Manages trace context lifecycle for distributed logging
 * Handles creation, propagation, and cleanup of trace contexts using correlation IDs
 */
public class TraceManager {

    private static final Logger logger = LoggerUtil.getLogger(TraceManager.class);

    /**
     * Creates a new trace context for a module operation
     *
     * @param moduleName the module name (e.g., "PRODUCT", "ORDER")
     * @param operation  the operation being performed
     * @return TraceContext containing the trace information
     */
    public static TraceContext createTraceContext(String moduleName, String operation) {
        return createTraceContext(moduleName, operation, null);
    }

    /**
     * Creates a new trace context for a module operation with user information
     *
     * @param moduleName the module name (e.g., "PRODUCT", "ORDER")
     * @param operation  the operation being performed
     * @param userId     the user ID (optional)
     * @return TraceContext containing the trace information
     */
    public static TraceContext createTraceContext(String moduleName, String operation, String userId) {
        String correlationId = LoggerUtil.initializeLoggingContext(moduleName, operation, userId);

        logger.debug("Created new trace context - CorrelationId: {}, Module: {}, Operation: {}",
                correlationId, moduleName, operation);

        return new TraceContext(correlationId, moduleName, operation, userId);
    }

    /**
     * Continues an existing trace context (for inter-service calls)
     * Use this when processing a request that originated from another service/module
     *
     * @param correlationId existing correlation ID
     * @param moduleName    the current module name
     * @param operation     the current operation
     * @return TraceContext with existing correlation ID
     */
    public static TraceContext continueTraceContext(String correlationId, String moduleName, String operation) {
        LoggerUtil.setCorrelationId(correlationId);
        LoggerUtil.setModuleName(moduleName);
        LoggerUtil.setOperation(operation);
        LoggerUtil.generateRequestId(); // Generate new request ID for this module

        logger.debug("Continued trace context - CorrelationId: {}, Module: {}, Operation: {}",
                correlationId, moduleName, operation);

        return new TraceContext(correlationId, moduleName, operation, LoggerUtil.getUserId());
    }

    /**
     * Continues an existing trace context with user information
     * Use this when processing a request that originated from another service/module
     *
     * @param correlationId existing correlation ID
     * @param moduleName    the current module name
     * @param operation     the current operation
     * @param userId        the user ID
     * @return TraceContext with existing correlation ID and user ID
     */
    public static TraceContext continueTraceContext(String correlationId, String moduleName, String operation, String userId) {
        LoggerUtil.setCorrelationId(correlationId);
        LoggerUtil.setModuleName(moduleName);
        LoggerUtil.setOperation(operation);
        LoggerUtil.setUserId(userId);
        LoggerUtil.generateRequestId(); // Generate new request ID for this module

        logger.debug("Continued trace context with user - CorrelationId: {}, Module: {}, Operation: {}, UserId: {}",
                correlationId, moduleName, operation, userId);

        return new TraceContext(correlationId, moduleName, operation, userId);
    }

    /**
     * Cleans up the trace context
     * Should be called at the end of request/operation processing
     */
    public static void cleanup() {
        String correlationId = LoggerUtil.getCorrelationId();
        if (correlationId != null) {
            logger.debug("Cleaning up trace context - CorrelationId: {}", correlationId);
        }
        LoggerUtil.clearMDC();
    }

    /**
     * Executes a traced operation with automatic cleanup
     *
     * @param moduleName      the module name
     * @param operation       the operation name
     * @param tracedOperation the operation to execute
     * @return the result of the operation
     */
    public static <T> T executeTraced(String moduleName, String operation, TracedOperation<T> tracedOperation) {
        TraceContext context = createTraceContext(moduleName, operation);
        long startTime = System.currentTimeMillis();

        try {
            LoggerUtil.logOperationStart(logger, operation, "Starting traced operation");
            T result = tracedOperation.execute();

            long duration = System.currentTimeMillis() - startTime;
            LoggerUtil.logOperationComplete(logger, operation, duration);

            return result;
        } catch (Exception e) {
            LoggerUtil.logOperationFailure(logger, operation, e);
            throw new RuntimeException("Operation failed: " + operation, e);
        } finally {
            cleanup();
        }
    }

    /**
     * Executes a traced operation with user context and automatic cleanup
     *
     * @param moduleName      the module name
     * @param operation       the operation name
     * @param userId          the user ID
     * @param tracedOperation the operation to execute
     * @return the result of the operation
     */
    public static <T> T executeTraced(String moduleName, String operation, String userId, TracedOperation<T> tracedOperation) {
        TraceContext context = createTraceContext(moduleName, operation, userId);
        long startTime = System.currentTimeMillis();

        try {
            LoggerUtil.logOperationStart(logger, operation, "Starting traced operation");
            T result = tracedOperation.execute();

            long duration = System.currentTimeMillis() - startTime;
            LoggerUtil.logOperationComplete(logger, operation, duration);

            return result;
        } catch (Exception e) {
            LoggerUtil.logOperationFailure(logger, operation, e);
            throw new RuntimeException("Operation failed: " + operation, e);
        } finally {
            cleanup();
        }
    }

    /**
     * Functional interface for traced operations
     *
     * @param <T> the return type of the operation
     */
    @FunctionalInterface
    public interface TracedOperation<T> {
        T execute() throws Exception;
    }

    /**
     * Immutable trace context information
     */
    @Getter
    public static class TraceContext {
        private final String correlationId;
        private final String moduleName;
        private final String operation;
        private final String userId;
        private final long timestamp;

        public TraceContext(String correlationId, String moduleName, String operation, String userId) {
            this.correlationId = correlationId;
            this.moduleName = moduleName;
            this.operation = operation;
            this.userId = userId;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("TraceContext{correlationId='%s', module='%s', operation='%s', userId='%s', timestamp=%d}",
                    correlationId, moduleName, operation, userId, timestamp);
        }
    }
} 