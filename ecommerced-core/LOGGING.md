# Enhanced Logging System

## Overview

The enhanced logging system provides comprehensive tracing and structured logging capabilities for the eCommerce platform. It includes trace ID generation, module-based request IDs, and centralized logging management.

## Features

### ✅ **Trace ID Management**
- Unique trace IDs for each request/operation
- Cross-service trace propagation
- Automatic trace context cleanup

### ✅ **Module-Based Request IDs**
- Module-specific request identification
- Format: `MODULE_TIMESTAMP_RANDOM`
- Hierarchical request tracking

### ✅ **Structured Logging**
- Consistent log format across modules
- MDC (Mapped Diagnostic Context) support
- Multiple log appenders (console, file, error, audit, performance)

### ✅ **Audit Trail**
- Business event logging
- User action tracking
- Compliance-ready audit logs

### ✅ **Performance Monitoring**
- Operation timing
- Performance metrics logging
- Slow operation detection

## Core Components

### 1. LoggerUtil
Enhanced utility class for logging operations:
- Trace ID generation and management
- Module-based request ID creation
- MDC context management
- Performance logging helpers

### 2. TraceManager
Manages trace context lifecycle:
- Trace context creation and continuation
- Automatic cleanup
- Traced operation execution
- Inter-service call support

### 3. Enhanced Logback Configuration
Multiple log appenders:
- **Console**: Development logging
- **File**: Application logs  
- **Error**: Error-specific logs
- **Audit**: Business event logs
- **Performance**: Performance metrics
- **JSON**: Structured logging for microservices

## Usage Examples

### Basic Traced Operation
```java
// Initialize logging context
TraceManager.TraceContext context = TraceManager.createTraceContext("PRODUCT", "CREATE_PRODUCT");

try {
    logger.info("Starting product creation process");
    // Your business logic here
    logger.info("Product created successfully");
} finally {
    TraceManager.cleanup();
}
```

### Traced Operation with User Context
```java
TraceManager.TraceContext context = TraceManager.createTraceContext("INVENTORY", "UPDATE_STOCK", "user123");

try {
    logger.info("Starting inventory update");
    // Business logic
    auditLogger.info("Stock updated for product: {} by user: {}", "PROD-001", "user123");
} finally {
    TraceManager.cleanup();
}
```

### Automatic Traced Execution
```java
String result = TraceManager.executeTraced("ORDER", "PROCESS_ORDER", "customer456", () -> {
    logger.info("Processing order");
    // Order processing logic
    return "ORDER-12345";
});
```

### Inter-Service Call Continuation
```java
// Service A creates trace
TraceManager.TraceContext originalContext = TraceManager.createTraceContext("ORDER", "CREATE_ORDER");
String traceId = originalContext.getTraceId();

// Service B continues the trace
TraceManager.TraceContext inventoryContext = TraceManager.continueTraceContext(
    traceId, "INVENTORY", "RESERVE_ITEMS");
```

### Performance Monitoring
```java
TraceManager.TraceContext context = TraceManager.createTraceContext("ANALYTICS", "GENERATE_REPORT");
long startTime = System.currentTimeMillis();

try {
    // Long-running operation
    long duration = System.currentTimeMillis() - startTime;
    performanceLogger.info("Report generation completed in {} ms", duration);
} finally {
    TraceManager.cleanup();
}
```

## MDC Fields

The following fields are automatically included in log messages:

| Field | Description | Example |
|-------|-------------|---------|
| `traceId` | Unique trace identifier | `a1b2c3d4e5f6g7h8` |
| `moduleName` | Module name | `PRODUCT`, `INVENTORY`, `ORDER` |
| `moduleRequestId` | Module-specific request ID | `PRODUCT_1704067200000_1234` |
| `userId` | User identifier | `user123`, `customer456` |
| `operation` | Current operation | `CREATE_PRODUCT`, `UPDATE_STOCK` |
| `correlationId` | Cross-service correlation | `corr-abc123` |

## Log Output Format

### Console/File Logs
```
2024-01-06 19:30:45.123 [main] INFO  [a1b2c3d4] [PRODUCT] [PRODUCT_1704067200000_1234] [user123] [CREATE_PRODUCT] com.alier.core.example.LoggingExample - Starting product creation process
```

### JSON Structured Logs
```json
{
  "timestamp": "2024-01-06 19:30:45.123",
  "level": "INFO",
  "thread": "main",
  "logger": "com.alier.core.example.LoggingExample",
  "traceId": "a1b2c3d4e5f6g7h8",
  "moduleName": "PRODUCT",
  "moduleRequestId": "PRODUCT_1704067200000_1234",
  "userId": "user123",
  "operation": "CREATE_PRODUCT",
  "message": "Starting product creation process"
}
```

## Module Integration

When creating new modules (product, inventory, order, etc.), initialize logging:

```java
public class ProductService {
    private static final Logger logger = LoggerUtil.getLogger(ProductService.class);
    
    public Product createProduct(CreateProductRequest request) {
        return TraceManager.executeTraced("PRODUCT", "CREATE_PRODUCT", request.getUserId(), () -> {
            logger.info("Creating product: {}", request.getName());
            
            // Business logic
            Product product = new Product();
            // ... product creation logic
            
            logger.info("Product created with ID: {}", product.getId());
            return product;
        });
    }
}
```

## Log Files

The following log files are created:

| File | Purpose | Retention |
|------|---------|-----------|
| `ecommerced-core.log` | Main application logs | 30 days |
| `ecommerced-core-error.log` | Error logs only | 90 days |
| `ecommerced-core-audit.log` | Business audit events | 365 days |
| `ecommerced-core-performance.log` | Performance metrics | 30 days |
| `ecommerced-core-json.log` | JSON structured logs | 30 days |

## Best Practices

### 1. **Always Use Trace Context**
```java
// ✅ Good
TraceManager.TraceContext context = TraceManager.createTraceContext("MODULE", "OPERATION");
try {
    // Business logic
} finally {
    TraceManager.cleanup();
}

// ❌ Bad - No trace context
logger.info("Processing started");
```

### 2. **Use Appropriate Log Levels**
```java
logger.debug("Detailed debug information");  // Development only
logger.info("Important business events");     // Production
logger.warn("Warning conditions");            // Production
logger.error("Error conditions", exception);  // Production
```

### 3. **Use Structured Logging**
```java
// ✅ Good - Structured
logger.info("Order created - ID: {}, Customer: {}, Amount: {}", 
    orderId, customerId, amount);

// ❌ Bad - Unstructured
logger.info("Order created: " + orderId + " for customer " + customerId);
```

### 4. **Audit Important Events**
```java
auditLogger.info("Payment processed - Amount: {}, Customer: {}, Method: {}", 
    amount, customerId, paymentMethod);
```

### 5. **Monitor Performance**
```java
long startTime = System.currentTimeMillis();
// ... operation
long duration = System.currentTimeMillis() - startTime;
performanceLogger.info("Operation completed in {} ms", duration);
```

## Configuration

### Development Environment
- Console logging enabled
- DEBUG level for com.alier packages
- All appenders active

### Production Environment
- Console logging disabled
- INFO level for com.alier packages
- File and audit logging only
- JSON logging for log aggregation

## Testing

Run the logging examples:
```bash
mvn compile exec:java -Dexec.mainClass="com.alier.core.example.LoggingExample"
```

This will demonstrate all logging features and create sample log files.

## Future Enhancements

- [ ] Elasticsearch integration for log aggregation
- [ ] Metrics collection (Prometheus/Micrometer)
- [ ] Log sampling for high-volume operations
- [ ] Distributed tracing with OpenTelemetry
- [ ] Log correlation across multiple services
- [ ] Custom log filters and transformations 