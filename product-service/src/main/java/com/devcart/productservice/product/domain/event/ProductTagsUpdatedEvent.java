package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductTag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Domain event fired when product tags are updated.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductTagsUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final Set<ProductTag> oldTags;
    private final Set<ProductTag> newTags;

    public ProductTagsUpdatedEvent(ProductId productId, Set<ProductTag> oldTags, Set<ProductTag> newTags) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.oldTags = oldTags;
        this.newTags = newTags;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductTagsUpdated";
    }
} 