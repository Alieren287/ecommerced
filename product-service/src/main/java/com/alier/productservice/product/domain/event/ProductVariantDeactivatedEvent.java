package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;

import java.time.LocalDateTime;

public record ProductVariantDeactivatedEvent(
        ProductId productId,
        ProductVariantId variantId,
        LocalDateTime occurredOn) implements DomainEvent {

    public ProductVariantDeactivatedEvent(ProductId productId, ProductVariantId variantId) {
        this(productId, variantId, LocalDateTime.now());
    }
}
