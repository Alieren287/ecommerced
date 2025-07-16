package com.alier.ecommerced.core.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base interface for all domain events in the hexagonal architecture.
 * Domain events represent something that happened in the domain that domain experts care about.
 */
public interface DomainEvent {

    /**
     * Unique identifier for the event
     */
    UUID getEventId();

    /**
     * When the event occurred
     */
    LocalDateTime getOccurredAt();

    /**
     * The aggregate ID that generated this event
     */
    UUID getAggregateId();

    /**
     * The type of event for identification purposes
     */
    String getEventType();
} 