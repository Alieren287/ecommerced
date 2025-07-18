package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductName;

import java.time.LocalDateTime;

public record ProductCreatedEvent(
        ProductId productId,
        ProductName productName,
        LocalDateTime occurredOn) implements DomainEvent {

    public ProductCreatedEvent(ProductId productId, ProductName productName) {
        this(productId, productName, LocalDateTime.now());
    }
}