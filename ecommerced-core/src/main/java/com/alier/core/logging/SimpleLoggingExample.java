package com.alier.core.logging;

import org.slf4j.Logger;

/**
 * Simple example demonstrating the simplified logging system
 * in a typical ecommerce product creation flow
 */
public class SimpleLoggingExample {

    private static final Logger logger = LoggerUtil.getLogger(SimpleLoggingExample.class);

    /**
     * Example: Product Controller (Entry Point)
     * This is where logging context is initialized
     */
    public void productControllerExample() {
        // Step 1: Initialize logging context at entry point
        TraceManager.TraceContext context = TraceManager.createTraceContext("PRODUCT", "create-product", "user123");

        try {
            logger.info("Received request to create product");

            // Step 2: Call business services (context propagates automatically)
            validateProduct();
            saveProduct();
            updateInventory();

            logger.info("Product creation completed successfully");

        } catch (Exception e) {
            logger.error("Product creation failed", e);
            throw e;
        } finally {
            // Step 3: Always clean up context
            TraceManager.cleanup();
        }
    }

    /**
     * Example: Business Service (No Context Initialization)
     * Services use the existing context from the controller
     */
    private void validateProduct() {
        // NO context initialization - context already exists
        logger.info("Starting product validation");

        // Your validation logic here
        logger.debug("Checking product name uniqueness");
        logger.debug("Validating product price");
        logger.debug("Checking required fields");

        logger.info("Product validation completed");
    }

    /**
     * Example: Repository Service (Context Propagation)
     */
    private void saveProduct() {
        logger.info("Saving product to database");

        // Database operation
        logger.debug("Executing SQL insert");

        // Log to audit trail for business events
        Logger auditLogger = LoggerUtil.getAuditLogger();
        auditLogger.info("Product {} created by user {}", "product-123", LoggerUtil.getUserId());

        logger.info("Product saved successfully");
    }

    /**
     * Example: Cross-Module Call (Continue Trace Context)
     */
    private void updateInventory() {
        // When calling another module, continue the trace context
        String currentTraceId = LoggerUtil.getTraceId();

        TraceManager.TraceContext inventoryContext = TraceManager.continueTraceContext(
                currentTraceId,
                "INVENTORY",
                "update-stock"
        );

        try {
            logger.info("Updating inventory for new product");

            // Inventory module logic
            logger.debug("Setting initial stock level");
            logger.debug("Creating inventory record");

            logger.info("Inventory update completed");

        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example: External API Call (With Correlation ID)
     */
    public void externalApiExample() {
        // When processing request from external system
        String correlationId = "external-order-789"; // From external system

        TraceManager.TraceContext context = TraceManager.continueTraceContext(
                LoggerUtil.generateTraceId(),
                correlationId,
                "PAYMENT",
                "process-external-payment"
        );

        try {
            logger.info("Processing payment from external system");

            // External API call logic
            logger.debug("Calling payment gateway");

            // Log performance metrics
            Logger perfLogger = LoggerUtil.getPerformanceLogger();
            perfLogger.info("Payment processing took 250ms");

            logger.info("External payment processed successfully");

        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example: Automatic Execution (Simplified)
     */
    public void automaticExecutionExample() {
        // For simple operations, use automatic cleanup
        String result = TraceManager.executeTraced("PRODUCT", "calculate-discount", () -> {
            logger.info("Calculating product discount");

            // Your calculation logic
            double discount = 0.15;
            logger.debug("Applied discount: {}%", discount * 100);

            return "15% discount applied";
        });

        // No manual cleanup needed
        logger.info("Discount calculation result: {}", result);
    }

    /**
     * TYPICAL LOG OUTPUT EXAMPLE:
     *
     * 2024-01-01 12:00:00.123 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductController - Received request to create product
     * 2024-01-01 12:00:00.135 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductService - Starting product validation
     * 2024-01-01 12:00:00.145 [http-nio-8080-exec-1] DEBUG [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductService - Checking product name uniqueness
     * 2024-01-01 12:00:00.155 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductService - Product validation completed
     * 2024-01-01 12:00:00.165 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductRepository - Saving product to database
     * 2024-01-01 12:00:00.175 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [INVENTORY] [1704067200000_5678] [user123] [update-stock] com.alier.inventory.InventoryService - Updating inventory for new product
     * 2024-01-01 12:00:00.185 [http-nio-8080-exec-1] INFO  [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] com.alier.product.ProductController - Product creation completed successfully
     *
     * AUDIT LOG:
     * 2024-01-01 12:00:00.170 [AUDIT] [a1b2c3d4e5f6] [PRODUCT] [1704067200000_1234] [user123] [create-product] - Product product-123 created by user user123
     *
     * PERFORMANCE LOG:
     * 2024-01-01 12:00:00.190 [PERF] [a1b2c3d4e5f6] [PAYMENT] [1704067200000_9999] [process-external-payment] - Payment processing took 250ms
     */
} 