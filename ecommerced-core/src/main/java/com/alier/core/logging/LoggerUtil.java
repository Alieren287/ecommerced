package com.alier.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simplified utility class for centralized logging functionality with correlation ID,
 * single request ID, and structured logging support.
 * <p>
 * This class uses a single ThreadLocal<Map> for thread-safe context management and is designed
 * to work seamlessly with @Slf4j annotation.
 */
public class LoggerUtil {

    // MDC Keys
    private static final String CORRELATION_ID = "correlationId";
    private static final String REQUEST_ID = "requestId";
    private static final String MODULE_NAME = "moduleName";
    private static final String USER_ID = "userId";
    private static final String OPERATION = "operation";

    // Single ThreadLocal context holder for thread safety
    private static final ThreadLocal<Map<String, String>> contextHolder =
            ThreadLocal.withInitial(HashMap::new);

    /**
     * Gets a logger for the specified class
     * Use this if you're not using @Slf4j annotation
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
     * Generates a new correlation ID and sets it in both ThreadLocal and MDC
     * This ID will be used for both internal multi-module tracking and external cross-system tracking
     * Call this at the beginning of each request/operation
     *
     * @return the generated correlation ID
     */
    public static String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString().replace("-", "");
        setCorrelationId(correlationId);
        return correlationId;
    }

    /**
     * Gets the current correlation ID from ThreadLocal
     *
     * @return the correlation ID
     */
    public static String getCorrelationId() {
        return contextHolder.get().get(CORRELATION_ID);
    }

    /**
     * Sets the correlation ID in both ThreadLocal and MDC
     * Use this when receiving a request from another service/system
     *
     * @param correlationId the correlation ID
     */
    public static void setCorrelationId(String correlationId) {
        contextHolder.get().put(CORRELATION_ID, correlationId);
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
        setRequestId(requestId);
        return requestId;
    }

    /**
     * Gets the request ID from ThreadLocal
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return contextHolder.get().get(REQUEST_ID);
    }

    /**
     * Sets the request ID in both ThreadLocal and MDC
     *
     * @param requestId the request ID
     */
    public static void setRequestId(String requestId) {
        contextHolder.get().put(REQUEST_ID, requestId);
        MDC.put(REQUEST_ID, requestId);
    }

    /**
     * Gets the module name from ThreadLocal
     *
     * @return the module name
     */
    public static String getModuleName() {
        return contextHolder.get().get(MODULE_NAME);
    }

    /**
     * Sets the module name in both ThreadLocal and MDC
     *
     * @param moduleName the module name
     */
    public static void setModuleName(String moduleName) {
        contextHolder.get().put(MODULE_NAME, moduleName);
        MDC.put(MODULE_NAME, moduleName);
    }

    /**
     * Gets the user ID from ThreadLocal
     *
     * @return the user ID
     */
    public static String getUserId() {
        return contextHolder.get().get(USER_ID);
    }

    /**
     * Sets the user ID in both ThreadLocal and MDC for audit trail
     *
     * @param userId the user ID
     */
    public static void setUserId(String userId) {
        contextHolder.get().put(USER_ID, userId);
        MDC.put(USER_ID, userId);
    }

    /**
     * Gets the operation name from ThreadLocal
     *
     * @return the operation name
     */
    public static String getOperation() {
        return contextHolder.get().get(OPERATION);
    }

    /**
     * Sets the operation name in both ThreadLocal and MDC for tracking
     *
     * @param operation the operation name
     */
    public static void setOperation(String operation) {
        contextHolder.get().put(OPERATION, operation);
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
     * Initializes complete logging context for a module operation without user
     *
     * @param moduleName the module name (e.g., "PRODUCT", "ORDER")
     * @param operation  the operation being performed
     * @return the generated correlation ID
     */
    public static String initializeLoggingContext(String moduleName, String operation) {
        return initializeLoggingContext(moduleName, operation, null);
    }

    /**
     * Clears all ThreadLocal context and MDC
     * Call this at the end of request processing to prevent memory leaks
     */
    public static void clearContext() {
        // Clear ThreadLocal
        contextHolder.get().clear();

        // Clear MDC
        MDC.clear();
    }

    /**
     * Copies current context to another thread
     * Use this when spawning async tasks
     *
     * @return LoggingContext that can be applied to another thread
     */
    public static LoggingContext copyContext() {
        Map<String, String> currentContext = contextHolder.get();
        return new LoggingContext(
                currentContext.get(CORRELATION_ID),
                currentContext.get(REQUEST_ID),
                currentContext.get(MODULE_NAME),
                currentContext.get(USER_ID),
                currentContext.get(OPERATION)
        );
    }

    /**
     * Applies a logging context to current thread
     * Use this when starting async task processing
     *
     * @param context the logging context to apply
     */
    public static void applyContext(LoggingContext context) {
        if (context != null) {
            if (context.getCorrelationId() != null) {
                setCorrelationId(context.getCorrelationId());
            }
            if (context.getRequestId() != null) {
                setRequestId(context.getRequestId());
            }
            if (context.getModuleName() != null) {
                setModuleName(context.getModuleName());
            }
            if (context.getUserId() != null) {
                setUserId(context.getUserId());
            }
            if (context.getOperation() != null) {
                setOperation(context.getOperation());
            }
        }
    }

    /**
     * Logs method entry with parameters
     * Use with @Slf4j: LoggerUtil.logMethodEntry(log, "methodName", param1, param2);
     *
     * @param logger     the logger instance (usually from @Slf4j)
     * @param methodName the method name
     * @param parameters the method parameters
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Entering method: ").append(methodName);
            if (parameters != null && parameters.length > 0) {
                sb.append(" with parameters: [");
                for (int i = 0; i < parameters.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(parameters[i]);
                }
                sb.append("]");
            }
            logger.debug(sb.toString());
        }
    }

    /**
     * Logs method exit with result
     * Use with @Slf4j: LoggerUtil.logMethodExit(log, "methodName", result);
     *
     * @param logger     the logger instance (usually from @Slf4j)
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
     * Use with @Slf4j: LoggerUtil.logMethodExit(log, "methodName");
     *
     * @param logger     the logger instance (usually from @Slf4j)
     * @param methodName the method name
     */
    public static void logMethodExit(Logger logger, String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {}", methodName);
        }
    }

    /**
     * Logs operation start
     * Use with @Slf4j: LoggerUtil.logOperationStart(log, "CREATE_ORDER", "Order details");
     *
     * @param logger    the logger instance (usually from @Slf4j)
     * @param operation the operation name
     * @param details   additional details
     */
    public static void logOperationStart(Logger logger, String operation, String details) {
        logger.info("Starting operation: {} - {}", operation, details);
    }

    /**
     * Logs operation completion with duration
     * Use with @Slf4j: LoggerUtil.logOperationComplete(log, "CREATE_ORDER", duration);
     *
     * @param logger    the logger instance (usually from @Slf4j)
     * @param operation the operation name
     * @param duration  the operation duration in milliseconds
     */
    public static void logOperationComplete(Logger logger, String operation, long duration) {
        logger.info("Completed operation: {} in {} ms", operation, duration);
    }

    /**
     * Logs operation failure
     * Use with @Slf4j: LoggerUtil.logOperationFailure(log, "CREATE_ORDER", exception);
     *
     * @param logger    the logger instance (usually from @Slf4j)
     * @param operation the operation name
     * @param error     the error that occurred
     */
    public static void logOperationFailure(Logger logger, String operation, Throwable error) {
        logger.error("Failed operation: {} - Error: {}", operation, error.getMessage(), error);
    }

    /**
     * Context holder for async operations
     */
    public static class LoggingContext {
        private final String correlationId;
        private final String requestId;
        private final String moduleName;
        private final String userId;
        private final String operation;

        public LoggingContext(String correlationId, String requestId, String moduleName, String userId, String operation) {
            this.correlationId = correlationId;
            this.requestId = requestId;
            this.moduleName = moduleName;
            this.userId = userId;
            this.operation = operation;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getUserId() {
            return userId;
        }

        public String getOperation() {
            return operation;
        }
    }
} 