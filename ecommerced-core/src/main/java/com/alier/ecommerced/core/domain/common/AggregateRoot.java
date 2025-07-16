package com.alier.ecommerced.core.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for aggregate roots in the hexagonal architecture.
 * Aggregate roots are the only entities that can be referenced from outside the aggregate.
 * They are responsible for maintaining the consistency of the entire aggregate.
 */
public abstract class AggregateRoot extends Entity {

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(UUID id) {
        super(id);
    }

    protected AggregateRoot(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
    }

    /**
     * Publishes a domain event from this aggregate root.
     *
     * @param event the domain event to publish
     */
    protected void publishEvent(DomainEvent event) {
        addDomainEvent(event);
    }

    /**
     * Template method for aggregate validation.
     * Should be implemented by concrete aggregate roots to validate business rules.
     */
    protected abstract void validate();

    /**
     * Template method for business rule validation.
     *
     * @param condition the condition that must be true
     * @param message   the error message if condition is false
     */
    protected void businessRequire(boolean condition, String message) {
        if (!condition) {
            throw new BusinessRuleViolationException(message);
        }
    }
} 