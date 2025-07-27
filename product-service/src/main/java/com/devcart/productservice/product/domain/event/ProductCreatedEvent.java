package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductName;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductCreatedEvent implements DomainEvent {

    private final UUID eventId;
    private final ProductId productId;
    private final ProductName productName;
    private final LocalDateTime occurredOn;

    public ProductCreatedEvent(ProductId productId, ProductName productName, LocalDateTime occurredOn) {
        this.eventId = UUID.randomUUID();
        this.productId = productId;
        this.productName = productName;
        this.occurredOn = occurredOn;
    }

    public ProductCreatedEvent(ProductId productId, ProductName productName) {
        this(productId, productName, LocalDateTime.now());
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredOn;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductCreated";
    }
}