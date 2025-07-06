package com.alier.core.example;

import com.alier.core.logging.LoggerUtil;
import com.alier.core.logging.TraceManager;
import com.alier.core.logging.ModuleNames;
import org.slf4j.Logger;

/**
 * Example class demonstrating the enhanced logging system with trace IDs,
 * module-based request IDs, and structured logging
 */
public class LoggingExample {

    private static final Logger logger = LoggerUtil.getLogger(LoggingExample.class);
    private static final Logger auditLogger = LoggerUtil.getAuditLogger();
    private static final Logger performanceLogger = LoggerUtil.getPerformanceLogger();

    /**
     * Example 1: Basic traced operation
     */
    public void basicTracedOperation() {
        // Initialize logging context
        TraceManager.TraceContext context = TraceManager.createTraceContext(ModuleNames.PRODUCT, "CREATE_PRODUCT");
        
        try {
            logger.info("Starting product creation process");
            
            // Simulate some work
            Thread.sleep(100);
            
            logger.debug("Product validation completed");
            logger.info("Product created successfully");
            
        } catch (InterruptedException e) {
            logger.error("Product creation failed", e);
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example 2: Traced operation with user context
     */
    public void tracedOperationWithUser() {
        TraceManager.TraceContext context = TraceManager.createTraceContext(ModuleNames.INVENTORY, "UPDATE_STOCK", "user123");
        
        try {
            logger.info("Starting inventory update");
            
            // Simulate business logic
            performStockUpdate();
            
            // Log audit event
            auditLogger.info("Stock updated for product: {} by user: {}", "PROD-001", "user123");
            
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example 3: Using automatic traced execution
     */
    public String automaticTracedExecution() {
        return TraceManager.executeTraced(ModuleNames.ORDER, "PROCESS_ORDER", "customer456", () -> {
            logger.info("Processing order");
            
            // Simulate order processing
            Thread.sleep(200);
            
            logger.info("Order processed successfully");
            return "ORDER-12345";
        });
    }

    /**
     * Example 4: Inter-service call continuation
     */
    public void interServiceCallExample() {
        // Original service creates trace
        TraceManager.TraceContext originalContext = TraceManager.createTraceContext(ModuleNames.ORDER, "CREATE_ORDER");
        String traceId = originalContext.getTraceId();
        
        try {
            logger.info("Order service processing");
            
            // Simulate call to another service
            callInventoryService(traceId);
            
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Simulates a call to inventory service with trace continuation
     */
    private void callInventoryService(String traceId) {
        // Inventory service continues the trace
        TraceManager.TraceContext inventoryContext = TraceManager.continueTraceContext(traceId, ModuleNames.INVENTORY, "RESERVE_ITEMS");
        
        try {
            logger.info("Inventory service reserving items");
            
            // Simulate inventory work
            Thread.sleep(50);
            
            logger.info("Items reserved successfully");
            
        } catch (InterruptedException e) {
            logger.error("Inventory reservation failed", e);
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example 5: Performance monitoring
     */
    public void performanceMonitoringExample() {
        TraceManager.TraceContext context = TraceManager.createTraceContext(ModuleNames.ANALYTICS, "GENERATE_REPORT");
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Starting report generation");
            
            // Simulate long-running operation
            Thread.sleep(500);
            
            long duration = System.currentTimeMillis() - startTime;
            performanceLogger.info("Report generation completed in {} ms", duration);
            
            if (duration > 1000) {
                logger.warn("Report generation took longer than expected: {} ms", duration);
            }
            
        } catch (InterruptedException e) {
            logger.error("Report generation failed", e);
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Example 6: Complex business operation with multiple steps
     */
    public void complexBusinessOperation() {
        TraceManager.TraceContext context = TraceManager.createTraceContext(ModuleNames.ECOMMERCE, "COMPLETE_PURCHASE", "customer789");
        
        try {
            logger.info("Starting purchase completion process");
            
            // Step 1: Validate payment
            LoggerUtil.setOperation("VALIDATE_PAYMENT");
            logger.info("Validating payment information");
            auditLogger.info("Payment validation initiated for amount: {}", 99.99);
            
            // Step 2: Process inventory
            LoggerUtil.setOperation("PROCESS_INVENTORY");
            logger.info("Processing inventory updates");
            performStockUpdate();
            
            // Step 3: Create order
            LoggerUtil.setOperation("CREATE_ORDER");
            logger.info("Creating order record");
            
            // Step 4: Send notifications
            LoggerUtil.setOperation("SEND_NOTIFICATIONS");
            logger.info("Sending confirmation notifications");
            
            auditLogger.info("Purchase completed successfully for customer: {}", "customer789");
            
        } finally {
            TraceManager.cleanup();
        }
    }

    /**
     * Simulates stock update operation
     */
    private void performStockUpdate() {
        try {
            logger.debug("Checking current stock levels");
            Thread.sleep(50);
            
            logger.debug("Updating stock quantities");
            Thread.sleep(30);
            
            logger.info("Stock update completed");
            
        } catch (InterruptedException e) {
            logger.error("Stock update failed", e);
        }
    }

    /**
     * Main method to demonstrate logging examples
     */
    public static void main(String[] args) {
        LoggingExample example = new LoggingExample();
        
        System.out.println("=== Enhanced Logging Examples ===\n");
        
        System.out.println("1. Basic traced operation:");
        example.basicTracedOperation();
        
        System.out.println("\n2. Traced operation with user:");
        example.tracedOperationWithUser();
        
        System.out.println("\n3. Automatic traced execution:");
        String result = example.automaticTracedExecution();
        System.out.println("Result: " + result);
        
        System.out.println("\n4. Inter-service call example:");
        example.interServiceCallExample();
        
        System.out.println("\n5. Performance monitoring:");
        example.performanceMonitoringExample();
        
        System.out.println("\n6. Complex business operation:");
        example.complexBusinessOperation();
        
        System.out.println("\n=== Logging Examples Complete ===");
    }
} 