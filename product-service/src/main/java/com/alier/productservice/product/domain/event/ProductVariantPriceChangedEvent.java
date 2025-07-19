package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;

import java.time.LocalDateTime;

public record ProductVariantPriceChangedEvent(
        ProductId productId,
        ProductVariantId variantId,
        Money oldPrice,
        Money newPrice,
        LocalDateTime occurredOn) implements DomainEvent {

    public ProductVariantPriceChangedEvent(ProductId productId, ProductVariantId variantId, Money oldPrice, Money newPrice) {
        this(productId, variantId, oldPrice, newPrice, LocalDateTime.now());
    }
}
