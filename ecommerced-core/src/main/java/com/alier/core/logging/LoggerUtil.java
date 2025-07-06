package com.alier.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simplified utility class for centralized logging functionality with correlation ID,
 * single request ID, and structured logging support
 */
public class LoggerUtil {

    // MDC Keys
    private static final String CORRELATION_ID = "correlationId";
    private static final String REQUEST_ID = "requestId";
    private static final String MODULE_NAME = "moduleName";
    private static final String USER_ID = "userId";
    private static final String OPERATION = "operation";

    /**
     * Gets a logger for the specified class
     *
     * @param clazz the class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Gets a logger for the specified name
     * Used for specialized loggers (audit, performance, etc.)
     *
     * @param name the logger name
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * Gets the audit logger for business events
     *
     * @return Audit logger instance
     */
    public static Logger getAuditLogger() {
        return LoggerFactory.getLogger("com.alier.audit");
    }

    /**
     * Gets the performance logger for metrics
     *
     * @return Performance logger instance
     */
    public static Logger getPerformanceLogger() {
        return LoggerFactory.getLogger("com.alier.performance");
    }

    /**
     * Generates a new correlation ID and sets it in MDC
     * This ID will be used for both internal multi-module tracking and external cross-system tracking
     * Call this at the beginning of each request/operation
     *
     * @return the generated correlation ID
     */
    public static String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(CORRELATION_ID, correlationId);
        return correlationId;
    }

    /**
     * Gets the current correlation ID from MDC
     *
     * @return the correlation ID
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }

    /**
     * Sets the correlation ID in MDC
     * Use this when receiving a request from another service/system
     *
     * @param correlationId the correlation ID
     */
    public static void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
    }

    /**
     * Generates a simple request ID
     * Format: TIMESTAMP_RANDOM (e.g., 1704067200000_1234)
     *
     * @return the generated request ID
     */
    public static String generateRequestId() {
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        String requestId = String.format("%d_%d", timestamp, random);
        MDC.put(REQUEST_ID, requestId);
        return requestId;
    }

    /**
     * Gets the request ID from MDC
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Sets the request ID in MDC
     *
     * @param requestId the request ID
     */
    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }

    /**
     * Gets the module name from MDC
     *
     * @return the module name
     */
    public static String getModuleName() {
        return MDC.get(MODULE_NAME);
    }

    /**
     * Sets the module name in MDC
     *
     * @param moduleName the module name
     */
    public static void setModuleName(String moduleName) {
        MDC.put(MODULE_NAME, moduleName);
    }

    /**
     * Gets the user ID from MDC
     *
     * @return the user ID
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Sets the user ID in MDC for audit trail
     *
     * @param userId the user ID
     */
    public static void setUserId(String userId) {
        MDC.put(USER_ID, userId);
    }

    /**
     * Gets the operation name from MDC
     *
     * @return the operation name
     */
    public static String getOperation() {
        return MDC.get(OPERATION);
    }

    /**
     * Sets the operation name in MDC for tracking
     *
     * @param operation the operation name
     */
    public static void setOperation(String operation) {
        MDC.put(OPERATION, operation);
    }

    /**
     * Initializes complete logging context for a module operation
     * Creates new correlation ID and request ID
     *
     * @param moduleName the module name (e.g., "PRODUCT", "ORDER")
     * @param operation  the operation being performed
     * @param userId     the user ID (optional)
     * @return the generated correlation ID
     */
    public static String initializeLoggingContext(String moduleName, String operation, String userId) {
        String correlationId = generateCorrelationId();
        generateRequestId();
        setModuleName(moduleName);
        setOperation(operation);
        if (userId != null) {
            setUserId(userId);
        }
        return correlationId;
    }

    /**
     * Initializes complete logging context for a module operation without user ID
     *
     * @param moduleName the module name
     * @param operation  the operation being performed
     * @return the generated correlation ID
     */
    public static String initializeLoggingContext(String moduleName, String operation) {
        return initializeLoggingContext(moduleName, operation, null);
    }

    /**
     * Clears all MDC values
     */
    public static void clearMDC() {
        MDC.clear();
    }

    // Convenience methods for structured logging

    /**
     * Logs method entry with parameters
     *
     * @param logger     the logger to use
     * @param methodName the method name
     * @param parameters the method parameters
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Entering method: ").append(methodName);
            if (parameters != null && parameters.length > 0) {
                sb.append(" with parameters: ");
                for (int i = 0; i < parameters.length; i++) {
                    sb.append(parameters[i]);
                    if (i < parameters.length - 1) sb.append(", ");
                }
            }
            logger.debug(sb.toString());
        }
    }

    /**
     * Logs method exit with result
     *
     * @param logger     the logger to use
     * @param methodName the method name
     * @param result     the method result
     */
    public static void logMethodExit(Logger logger, String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {} with result: {}", methodName, result);
        }
    }

    /**
     * Logs method exit without result
     *
     * @param logger     the logger to use
     * @param methodName the method name
     */
    public static void logMethodExit(Logger logger, String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {}", methodName);
        }
    }

    /**
     * Logs operation start
     *
     * @param logger    the logger to use
     * @param operation the operation name
     * @param details   additional details
     */
    public static void logOperationStart(Logger logger, String operation, String details) {
        logger.info("Starting operation: {} - {}", operation, details);
    }

    /**
     * Logs operation completion with duration
     *
     * @param logger    the logger to use
     * @param operation the operation name
     * @param duration  the operation duration in milliseconds
     */
    public static void logOperationComplete(Logger logger, String operation, long duration) {
        logger.info("Completed operation: {} in {}ms", operation, duration);
    }

    /**
     * Logs operation failure
     *
     * @param logger    the logger to use
     * @param operation the operation name
     * @param error     the error that occurred
     */
    public static void logOperationFailure(Logger logger, String operation, Throwable error) {
        logger.error("Failed operation: {} - {}", operation, error.getMessage(), error);
    }
} 