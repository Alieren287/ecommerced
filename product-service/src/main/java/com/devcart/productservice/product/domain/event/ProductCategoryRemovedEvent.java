package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when a category is removed from a product.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductCategoryRemovedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final UUID categoryId;

    public ProductCategoryRemovedEvent(ProductId productId, UUID categoryId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.categoryId = categoryId;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductCategoryRemoved";
    }
} 