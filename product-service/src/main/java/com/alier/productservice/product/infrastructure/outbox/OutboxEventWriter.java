package com.alier.productservice.product.infrastructure.outbox;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.infrastructure.outbox.persistence.OutboxEventJpaEntity;
import com.alier.productservice.product.infrastructure.outbox.persistence.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for writing domain events to the outbox table.
 * This ensures that domain events are persisted atomically with business data
 * within the same database transaction.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventWriter {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Saves a single domain event to the outbox table.
     * This method should be called within the same transaction as the business operation.
     *
     * @param domainEvent The domain event to persist
     */
    public void saveEvent(DomainEvent domainEvent) {
        try {
            String payload = serializeEvent(domainEvent);
            String aggregateType = determineAggregateType(domainEvent);

            OutboxEventJpaEntity outboxEvent = OutboxEventJpaEntity.create(
                    domainEvent.getAggregateId(),
                    aggregateType,
                    domainEvent.getClass().getName(),
                    payload
            );

            outboxEventRepository.save(outboxEvent);

            log.debug("Saved domain event to outbox: eventType={}, aggregateId={}, eventId={}",
                    domainEvent.getEventType(),
                    domainEvent.getAggregateId(),
                    domainEvent.getEventId());

        } catch (Exception e) {
            log.error("Failed to save domain event to outbox: eventType={}, aggregateId={}, error={}",
                    domainEvent.getEventType(),
                    domainEvent.getAggregateId(),
                    e.getMessage(), e);
            throw new OutboxEventException("Failed to save domain event to outbox", e);
        }
    }

    /**
     * Saves multiple domain events to the outbox table in a batch.
     * This method should be called within the same transaction as the business operation.
     *
     * @param domainEvents The list of domain events to persist
     */
    public void saveEvents(List<DomainEvent> domainEvents) {
        if (domainEvents == null || domainEvents.isEmpty()) {
            return;
        }

        try {
            List<OutboxEventJpaEntity> outboxEvents = domainEvents.stream()
                    .map(this::createOutboxEvent)
                    .toList();

            outboxEventRepository.saveAll(outboxEvents);

            log.debug("Saved {} domain events to outbox", domainEvents.size());

        } catch (Exception e) {
            log.error("Failed to save domain events to outbox: count={}, error={}",
                    domainEvents.size(), e.getMessage(), e);
            throw new OutboxEventException("Failed to save domain events to outbox", e);
        }
    }

    /**
     * Creates an OutboxEventJpaEntity from a domain event.
     */
    private OutboxEventJpaEntity createOutboxEvent(DomainEvent domainEvent) {
        String payload = serializeEvent(domainEvent);
        String aggregateType = determineAggregateType(domainEvent);

        return OutboxEventJpaEntity.create(
                domainEvent.getAggregateId(),
                aggregateType,
                domainEvent.getClass().getName(),
                payload
        );
    }

    /**
     * Serializes a domain event to JSON string.
     */
    private String serializeEvent(DomainEvent domainEvent) {
        try {
            return objectMapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new OutboxEventException("Failed to serialize domain event to JSON", e);
        }
    }

    /**
     * Determines the aggregate type from the domain event.
     * This can be customized based on your domain event naming conventions.
     */
    private String determineAggregateType(DomainEvent domainEvent) {
        String eventType = domainEvent.getEventType();

        // Extract aggregate type from event type
        // Examples: "ProductCreated" -> "Product", "ProductVariantCreated" -> "ProductVariant"
        if (eventType.startsWith("ProductVariant")) {
            return "ProductVariant";
        } else if (eventType.startsWith("Product")) {
            return "Product";
        }

        // Fallback: try to extract from class name
        String className = domainEvent.getClass().getSimpleName();
        if (className.contains("ProductVariant")) {
            return "ProductVariant";
        } else if (className.contains("Product")) {
            return "Product";
        }

        // Default fallback
        return "Unknown";
    }

    /**
     * Custom exception for outbox event operations.
     */
    public static class OutboxEventException extends RuntimeException {
        public OutboxEventException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 