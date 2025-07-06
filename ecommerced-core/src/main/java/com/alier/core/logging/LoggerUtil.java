package com.alier.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for centralized logging functionality
 */
public class LoggerUtil {

    private static final String REQUEST_ID = "requestId";
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
     * Gets the request ID from MDC
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Sets the request ID in MDC for correlation
     *
     * @param requestId the request ID
     */
    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
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
     * Clears all MDC values
     */
    public static void clearMDC() {
        MDC.clear();
    }

    /**
     * Logs method entry with parameters
     *
     * @param logger     the logger instance
     * @param methodName the method name
     * @param parameters the method parameters
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method: {} with parameters: {}", methodName, parameters);
        }
    }

    /**
     * Logs method exit with result
     *
     * @param logger     the logger instance
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
     * @param logger     the logger instance
     * @param methodName the method name
     */
    public static void logMethodExit(Logger logger, String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {}", methodName);
        }
    }
} 