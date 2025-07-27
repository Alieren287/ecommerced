package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductVariantId;
import com.devcart.productservice.product.domain.valueobject.VariantName;
import com.devcart.productservice.product.domain.valueobject.VariantSku;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a product variant is created.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductVariantCreatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final ProductVariantId variantId;
    private final VariantName variantName;
    private final VariantSku variantSku;

    public ProductVariantCreatedEvent(ProductId productId, ProductVariantId variantId,
                                      VariantName variantName, VariantSku variantSku) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.variantId = variantId;
        this.variantName = variantName;
        this.variantSku = variantSku;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductVariantCreated";
    }
} 