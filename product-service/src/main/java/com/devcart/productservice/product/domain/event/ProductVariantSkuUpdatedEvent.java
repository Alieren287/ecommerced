package com.devcart.productservice.product.domain.event;

import com.devcart.ecommerced.core.domain.common.DomainEvent;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductVariantId;
import com.devcart.productservice.product.domain.valueobject.VariantSku;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductVariantSkuUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final ProductId productId;
    private final ProductVariantId variantId;
    private final VariantSku oldSku;
    private final VariantSku newSku;
    private final LocalDateTime occurredOn;

    public ProductVariantSkuUpdatedEvent(ProductId productId, ProductVariantId variantId, VariantSku oldSku, VariantSku newSku, LocalDateTime occurredOn) {
        this.eventId = UUID.randomUUID();
        this.productId = productId;
        this.variantId = variantId;
        this.oldSku = oldSku;
        this.newSku = newSku;
        this.occurredOn = occurredOn;
    }

    public ProductVariantSkuUpdatedEvent(ProductId productId, ProductVariantId variantId, VariantSku oldSku, VariantSku newSku) {
        this(productId, variantId, oldSku, newSku, LocalDateTime.now());
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredOn;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductVariantSkuUpdated";
    }
}
