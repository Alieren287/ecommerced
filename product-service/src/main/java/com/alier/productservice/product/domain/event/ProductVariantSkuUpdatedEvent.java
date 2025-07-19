package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import com.alier.productservice.product.domain.valueobject.VariantSku;

import java.time.LocalDateTime;

public record ProductVariantSkuUpdatedEvent(
        ProductId productId,
        ProductVariantId variantId,
        VariantSku oldSku,
        VariantSku newSku,
        LocalDateTime occurredOn) implements DomainEvent {

    public ProductVariantSkuUpdatedEvent(ProductId productId, ProductVariantId variantId, VariantSku oldSku, VariantSku newSku) {
        this(productId, variantId, oldSku, newSku, LocalDateTime.now());
    }
}
