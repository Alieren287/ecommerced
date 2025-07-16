package com.alier.productservice.product.domain.event;

import com.alier.ecommerced.core.domain.common.DomainEvent;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductImage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain event fired when product images are updated.
 */
@Getter
@ToString
@EqualsAndHashCode
public class ProductImagesUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final ProductId productId;
    private final List<ProductImage> oldImages;
    private final List<ProductImage> newImages;

    public ProductImagesUpdatedEvent(ProductId productId, List<ProductImage> oldImages, List<ProductImage> newImages) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = LocalDateTime.now();
        this.productId = productId;
        this.oldImages = oldImages;
        this.newImages = newImages;
    }

    @Override
    public UUID getAggregateId() {
        return productId.getValue();
    }

    @Override
    public String getEventType() {
        return "ProductImagesUpdated";
    }
} 