# Outbox Pattern Implementation

This document describes the Outbox Pattern implementation in the product-service microservice for reliable event
publishing to Kafka.

## Overview

The Outbox Pattern ensures that domain events are published to Kafka in a reliable, atomic manner alongside business
data persistence. This prevents data inconsistency issues that can occur when business transactions succeed but event
publishing fails.

## Architecture

The implementation consists of four main components:

### 1. OutboxEventJpaEntity

- **Location**:
  `src/main/java/com/alier/productservice/product/infrastructure/outbox/persistence/OutboxEventJpaEntity.java`
- **Purpose**: JPA entity representing events in the `outbox_events` table
- **Key Fields**:
    - `id`: Unique identifier for the event
    - `aggregateType`: Type of aggregate (Product, ProductVariant)
    - `aggregateId`: ID of the aggregate that generated the event
    - `eventType`: Full class name of the domain event
    - `payload`: JSON serialized event data
    - `status`: PENDING, PUBLISHED, or FAILED
    - `deliveryAttempts`: Number of retry attempts

### 2. OutboxEventRepository

- **Location**:
  `src/main/java/com/alier/productservice/product/infrastructure/outbox/persistence/OutboxEventRepository.java`
- **Purpose**: Spring Data JPA repository for outbox event operations
- **Key Features**:
    - Concurrent-safe event polling with `FOR UPDATE SKIP LOCKED`
    - Retry logic for failed events
    - Cleanup operations for published events

### 3. OutboxEventWriter

- **Location**: `src/main/java/com/alier/productservice/product/infrastructure/outbox/OutboxEventWriter.java`
- **Purpose**: Saves domain events to the outbox table within business transactions
- **Integration**: Called by `ProductRepositoryImpl` after saving business data

### 4. OutboxEventRelayer

- **Location**: `src/main/java/com/alier/productservice/product/infrastructure/outbox/OutboxEventRelayer.java`
- **Purpose**: Scheduled service that polls outbox table and publishes events to Kafka
- **Key Features**:
    - Configurable polling interval
    - Batch processing
    - Retry logic with exponential backoff
    - Automatic cleanup of published events

## Database Schema

The outbox pattern requires an `outbox_events` table. SQL DDL is provided in:

- **Location**: `src/main/resources/sql/outbox_events_schema.sql`

### Table Structure

```sql
CREATE TABLE outbox_events
(
    id                UUID PRIMARY KEY,
    aggregate_type    VARCHAR(100) NOT NULL,
    aggregate_id      UUID         NOT NULL,
    event_type        VARCHAR(255) NOT NULL,
    payload           TEXT         NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    processed_at      TIMESTAMP,
    delivery_attempts INTEGER      NOT NULL DEFAULT 0,
    last_error        VARCHAR(1000)
);
```

## Configuration

### Application Properties

```yaml
# Outbox Pattern Configuration
outbox:
  relayer:
    # Poll for pending events every 10 seconds
    poll-interval-ms: 10000
    # Process up to 50 events per batch
    batch-size: 50
    # Maximum retry attempts for failed events
    max-retries: 3
    # Wait 5 minutes before retrying failed events
    retry-delay-minutes: 5
    # Kafka topic for publishing events
    kafka-topic: product-events
    # Cleanup published events daily at 2 AM
    cleanup-cron: "0 0 2 * * ?"

# Kafka Configuration
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false
```

### Environment-Specific Configuration

For production environments, consider:

- Increasing `batch-size` for higher throughput
- Adjusting `poll-interval-ms` based on latency requirements
- Setting appropriate `max-retries` based on your error handling strategy
- Using a production Kafka cluster endpoint

## Usage

### Automatic Integration

The Outbox Pattern is automatically integrated with your existing domain events. When you save a `Product` aggregate:

```java
@Service
@Transactional
public class ProductService {
    
    public void createProduct(CreateProductCommand command) {
        // Create product (this generates domain events)
        Product product = new Product(/* parameters */);
        
        // Save product (this automatically saves events to outbox)
        productRepository.save(product);
        
        // Events are automatically published by OutboxEventRelayer
    }
}
```

### Event Flow

1. Business operation modifies a `Product` aggregate
2. Domain events are added to the aggregate
3. `ProductRepositoryImpl.save()` persists both business data and events atomically
4. `OutboxEventRelayer` polls for pending events every 10 seconds
5. Events are published to Kafka with the aggregate ID as the message key
6. Successfully published events are marked as `PUBLISHED`
7. Failed events are retried up to `max-retries` times

## Monitoring

### Health Check Endpoint

- **URL**: `GET /api/v1/outbox/health`
- **Purpose**: Health status of the outbox system

```json
{
  "status": "UP",
  "pendingEvents": 5,
  "failedEvents": 0,
  "message": "Outbox system is functioning normally"
}
```

### Metrics Endpoint

- **URL**: `GET /api/v1/outbox/metrics`
- **Purpose**: Current outbox metrics

```json
{
  "pendingEvents": 12,
  "failedEvents": 2
}
```

## Event Ordering

Events for the same aggregate are published with the aggregate ID as the Kafka message key, ensuring order preservation
within each aggregate.

## Error Handling

### Retry Strategy

- Failed events are retried up to `max-retries` times
- Retry delay is configurable via `retry-delay-minutes`
- Events that exceed max retries remain in `FAILED` status for manual investigation

### Dead Letter Handling

Events that fail all retry attempts remain in the `outbox_events` table with status `FAILED`. Consider implementing:

- Alerting on high failed event counts
- Manual retry mechanisms for failed events
- Dead letter queue processing for permanent failures

## Performance Considerations

### Batch Processing

- Configure `batch-size` based on your throughput requirements
- Higher batch sizes improve throughput but increase memory usage

### Database Performance

- The implementation includes optimized indexes for efficient querying
- Consider partitioning the `outbox_events` table by date for high-volume systems

### Cleanup

- Published events are automatically cleaned up after 7 days
- Adjust cleanup schedule via `cleanup-cron` property

## Troubleshooting

### High Pending Event Count

- Check Kafka connectivity
- Verify `OutboxEventRelayer` is running (check logs)
- Increase `batch-size` or decrease `poll-interval-ms`

### High Failed Event Count

- Check Kafka broker health
- Review error messages in `last_error` column
- Verify event serialization compatibility

### Performance Issues

- Monitor database performance on `outbox_events` table
- Consider database connection pool sizing
- Check Kafka producer configuration

## Development Setup

### Prerequisites

- Kafka running on `localhost:9092`
- Database (H2 for development, PostgreSQL for production)

### Testing

The outbox pattern can be tested by:

1. Creating/updating products via REST API
2. Monitoring the `outbox_events` table
3. Checking Kafka topic for published events
4. Using monitoring endpoints to verify system health

## Production Deployment

### Database Migration

Run the SQL script in `src/main/resources/sql/outbox_events_schema.sql` to create the required table and indexes.

### Monitoring Setup

- Set up alerts on `/outbox/health` endpoint
- Monitor `/outbox/metrics` for operational insights
- Set up database monitoring for the `outbox_events` table

### Configuration Tuning

- Adjust batch sizes based on load testing results
- Configure appropriate retry strategies
- Set up log aggregation for outbox-related logs 