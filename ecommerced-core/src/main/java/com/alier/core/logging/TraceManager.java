package com.alier.core.logging;

import lombok.Getter;
import org.slf4j.Logger;

/**
 * Manages trace context lifecycle for distributed logging
 * Handles creation, propagation, and cleanup of trace contexts
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
        String traceId = LoggerUtil.initializeLoggingContext(moduleName, operation, userId);

        logger.debug("Created new trace context - TraceId: {}, Module: {}, Operation: {}",
                traceId, moduleName, operation);

        return new TraceContext(traceId, moduleName, operation, userId);
    }

    /**
     * Continues an existing trace context (for inter-service calls)
     * Use this when processing a request that originated from another service
     *
     * @param traceId    existing trace ID
     * @param moduleName the current module name
     * @param operation  the current operation
     * @return TraceContext with existing trace ID
     */
    public static TraceContext continueTraceContext(String traceId, String moduleName, String operation) {
        LoggerUtil.setTraceId(traceId);
        LoggerUtil.setModuleName(moduleName);
        LoggerUtil.setOperation(operation);
        LoggerUtil.generateRequestId(); // Generate new request ID for this module

        logger.debug("Continued trace context - TraceId: {}, Module: {}, Operation: {}",
                traceId, moduleName, operation);

        return new TraceContext(traceId, moduleName, operation, LoggerUtil.getUserId());
    }

    /**
     * Continues an existing trace context with correlation ID
     * Use this for cross-system request tracking
     *
     * @param traceId       existing trace ID
     * @param correlationId correlation ID for cross-system tracking
     * @param moduleName    the current module name
     * @param operation     the current operation
     * @return TraceContext with existing trace ID and correlation ID
     */
    public static TraceContext continueTraceContext(String traceId, String correlationId, String moduleName, String operation) {
        LoggerUtil.setTraceId(traceId);
        LoggerUtil.setCorrelationId(correlationId);
        LoggerUtil.setModuleName(moduleName);
        LoggerUtil.setOperation(operation);
        LoggerUtil.generateRequestId();

        logger.debug("Continued trace context with correlation - TraceId: {}, CorrelationId: {}, Module: {}, Operation: {}",
                traceId, correlationId, moduleName, operation);

        return new TraceContext(traceId, moduleName, operation, LoggerUtil.getUserId());
    }

    /**
     * Cleans up the trace context
     * Should be called at the end of request/operation processing
     */
    public static void cleanup() {
        String traceId = LoggerUtil.getTraceId();
        if (traceId != null) {
            logger.debug("Cleaning up trace context - TraceId: {}", traceId);
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
        private final String traceId;
        private final String moduleName;
        private final String operation;
        private final String userId;
        private final long timestamp;

        public TraceContext(String traceId, String moduleName, String operation, String userId) {
            this.traceId = traceId;
            this.moduleName = moduleName;
            this.operation = operation;
            this.userId = userId;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("TraceContext{traceId='%s', module='%s', operation='%s', userId='%s', timestamp=%d}",
                    traceId, moduleName, operation, userId, timestamp);
        }
    }
} 