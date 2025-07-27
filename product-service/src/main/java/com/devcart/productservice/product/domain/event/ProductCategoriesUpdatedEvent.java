package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Domain event fired when product categories are updated.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductCategoriesUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final Set<UUID> oldCategoryIds;
    private final Set<UUID> newCategoryIds;

    public ProductCategoriesUpdatedEvent(ProductId productId, Set<UUID> oldCategoryIds, Set<UUID> newCategoryIds) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.oldCategoryIds = oldCategoryIds;
        this.newCategoryIds = newCategoryIds;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductCategoriesUpdated";
    }
} 