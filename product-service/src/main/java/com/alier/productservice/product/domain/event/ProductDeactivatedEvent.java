package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when a product is deactivated.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductDeactivatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;

    public ProductDeactivatedEvent(ProductId productId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductDeactivated";
    }
} 