package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductVariantId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductVariantActivatedEvent implements DomainEvent {

    private final UUID eventId;
    private final ProductId productId;
    private final ProductVariantId variantId;
    private final LocalDateTime occurredOn;

    public ProductVariantActivatedEvent(ProductId productId, ProductVariantId variantId, LocalDateTime occurredOn) {
        this.eventId = UUID.randomUUID();
        this.productId = productId;
        this.variantId = variantId;
        this.occurredOn = occurredOn;
    }

    public ProductVariantActivatedEvent(ProductId productId, ProductVariantId variantId) {
        this(productId, variantId, LocalDateTime.now());
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
        return "ProductVariantActivated";
    }
}
