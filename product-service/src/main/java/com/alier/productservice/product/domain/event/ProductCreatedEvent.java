package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when a new product is created.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductCreatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final UUID productId;
    private final ProductName productName;

    public ProductCreatedEvent(UUID productId, ProductName productName) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.productName = productName;
    }

    @Override
    public UUID getAggregateId() {
        return productId;
    }

    @Override
    public String getEventType() {
        return "ProductCreated";
    }
} 