package com.alier.core.logging;

import org.slf4j.Logger;

/**
 * Best Practices and Examples for Logging in the Core Module
 * <p>
 * This class demonstrates how to properly use the simplified logging system
 * with clear explanations of each concept.
 */
public class LoggingBestPractices {

    private static final Logger logger = LoggerUtil.getLogger(LoggingBestPractices.class);

    /**
     * BEST PRACTICE 1: Initialize logging context at system entry points
     * <p>
     * System entry points are where external requests enter your application:
     * - HTTP Controllers (REST endpoints)
     * - Message Consumers (Kafka, RabbitMQ)
     * - Scheduled Jobs (@Scheduled methods)
     * - Async Processing (CompletableFuture)
     * - External API calls (third-party integrations)
     */
    public void controllerExample() {
        // In a controller method
        TraceManager.TraceContext context = TraceManager.createTraceContext("PRODUCT", "create-product", "user123");

        try {
            logger.info("Creating new product");
            // Your business logic here

            // Call other services (context propagates automatically)
            callOtherService();

        } finally {
            // Always clean up at the end
            TraceManager.cleanup();
        }
    }

    /**
     * BEST PRACTICE 2: Services should NOT initialize context
     * <p>
     * Service layer methods should use the existing context.
     * They should log normally without creating new contexts.
     */
    public void serviceExample() {
        // NO context initialization here - context already exists from controller
        logger.info("Processing product validation");

        // Your service logic here
        logger.debug("Validation completed successfully");
    }

    /**
     * BEST PRACTICE 3: Cross-system correlation
     * <p>
     * When calling external services or processing requests from other systems,
     * use correlation ID to track requests across system boundaries.
     */
    public void externalApiExample() {
        // When calling external API, pass correlation ID
        String correlationId = "order-12345"; // From external system

        TraceManager.TraceContext context = TraceManager.continueTraceContext(
                LoggerUtil.getTraceId(),
                correlationId,
                "PAYMENT",
                "process-payment"
        );

        try {
            logger.info("Processing payment for external order");
            // External API call logic

        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * BEST PRACTICE 4: Use specialized loggers for different purposes
     */
    public void specializedLoggingExample() {
        Logger auditLogger = LoggerUtil.getAuditLogger();
        Logger perfLogger = LoggerUtil.getPerformanceLogger();

        // Business events - goes to audit.log (365 days retention)
        auditLogger.info("User {} created product {}", "user123", "product456");

        // Performance metrics - goes to performance.log (30 days retention)
        perfLogger.info("Product creation took 150ms");

        // Regular application logs - goes to main log files
        logger.info("Product creation completed");
    }

    /**
     * BEST PRACTICE 5: Inter-service communication
     * <p>
     * When your service calls another service within the same system,
     * continue the trace context.
     */
    public void interServiceExample() {
        // Get current trace ID to pass to another service
        String currentTraceId = LoggerUtil.getTraceId();

        // When calling another service, continue the trace
        TraceManager.TraceContext context = TraceManager.continueTraceContext(
                currentTraceId,
                "INVENTORY",
                "check-stock"
        );

        try {
            logger.info("Checking inventory for product");
            // Service call logic

        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * BEST PRACTICE 6: Automatic execution with cleanup
     * <p>
     * For simple operations, use executeTraced for automatic cleanup.
     */
    public void automaticExecutionExample() {
        String result = TraceManager.executeTraced("PRODUCT", "calculate-price", () -> {
            logger.info("Calculating product price");
            // Your calculation logic
            return "calculated-price";
        });

        // No manual cleanup needed - handled automatically
    }

    /**
     * HELPER: Example of calling another service
     */
    private void callOtherService() {
        // This method runs in the same context - no initialization needed
        logger.info("Calling inventory service");
        // Service call logic
    }

    /**
     * LOGGING CONTEXT HIERARCHY EXPLANATION:
     *
     * traceId: Unique identifier for the entire request flow
     *   - Same throughout the entire request lifecycle
     *   - Used to correlate all logs for a single request
     *   - Format: UUID without dashes (e.g., "a1b2c3d4e5f6")
     *
     * requestId: Unique identifier for each module/service within the trace
     *   - Different for each service that processes the request
     *   - Format: timestamp_random (e.g., "1704067200000_1234")
     *   - Helps identify which service generated a specific log entry
     *
     * correlationId: External system identifier
     *   - Passed between different systems/applications
     *   - Used for end-to-end request tracking across system boundaries
     *   - Format: depends on external system (e.g., "order-12345")
     *
     * moduleName: Which module is processing (e.g., "PRODUCT", "ORDER")
     *
     * operation: What operation is being performed (e.g., "create-product")
     *
     * userId: Who is making the request (optional)
     *
     * EXAMPLE LOG OUTPUT:
     * 2024-01-01 12:00:00.123 [http-nio-8080-exec-1] INFO [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductService - Creating new product
     */
} 