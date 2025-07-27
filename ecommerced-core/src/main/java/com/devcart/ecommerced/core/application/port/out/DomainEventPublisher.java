package com.devcart.ecommerced.core.application.port.out;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.domain.common.DomainEvent;

import java.util.List;

/**
 * Output port for publishing domain events.
 * This interface defines how domain events should be published to external systems.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a single domain event.
     *
     * @param event the domain event to publish
     * @return success if published, failure otherwise
     */
    Result<Void> publish(DomainEvent event);

    /**
     * Publishes multiple domain events.
     *
     * @param events the domain events to publish
     * @return success if all published, failure otherwise
     */
    Result<Void> publishAll(List<DomainEvent> events);
} 