package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when a tag is removed from a product.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductTagRemovedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final ProductTag tag;

    public ProductTagRemovedEvent(ProductId productId, ProductTag tag) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.tag = tag;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductTagRemoved";
    }
} 