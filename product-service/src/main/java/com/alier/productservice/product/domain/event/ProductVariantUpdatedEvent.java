package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import com.alier.productservice.product.domain.valueobject.VariantName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a product variant is updated.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductVariantUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final ProductVariantId variantId;
    private final VariantName variantName;

    public ProductVariantUpdatedEvent(ProductId productId, ProductVariantId variantId,
                                      VariantName variantName) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.variantId = variantId;
        this.variantName = variantName;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductVariantUpdated";
    }
} 