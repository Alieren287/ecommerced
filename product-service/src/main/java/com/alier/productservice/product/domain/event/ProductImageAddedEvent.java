package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductImage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when an image is added to a product.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductImageAddedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final ProductImage image;

    public ProductImageAddedEvent(ProductId productId, ProductImage image) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.image = image;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductImageAdded";
    }
} 