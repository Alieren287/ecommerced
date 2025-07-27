package com.devcart.productservice.product.domain;

import com.devcart.ecommerced.core.domain.common.Entity;
import com.devcart.ecommerced.core.domain.shared.Money;
import com.devcart.productservice.product.domain.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Product variant entity representing a specific variation of a product.
 * This is not an aggregate root - it's part of the Product aggregate.
 */
@Getter
public class ProductVariant extends Entity {

    private final ProductId productId;
    private VariantName name;
    private VariantSku sku;
    private VariantAttributes attributes;
    private Money price;
    private boolean active;

    // For creating new variants
    public ProductVariant(ProductId productId, VariantName name, VariantSku sku,
                          VariantAttributes attributes, Money price) {
        super(); // Generate entity ID
        this.productId = productId;
        this.name = name;
        this.sku = sku;
        this.attributes = attributes;
        this.price = price;
        this.active = true;

        validate();
    }

    public ProductVariant(ProductVariantId variantId, ProductId productId, VariantName name,
                          VariantSku sku, VariantAttributes attributes, Money price, boolean active,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(variantId.getValue(), createdAt, updatedAt); // Use ProductVariantId as entity ID
        this.productId = productId;
        this.name = name;
        this.sku = sku;
        this.attributes = attributes;
        this.price = price;
        this.active = active;

        validate();
    }

    // Variant Operations

    /**
     * Updates the variant's basic information.
     */
    public void updateBasicInfo(VariantName name, VariantAttributes attributes) {
        this.name = name;
        this.attributes = attributes;
        markAsUpdated();
        validate();
    }

    /**
     * Updates the variant's SKU.
     */
    public void updateSku(VariantSku sku) {
        this.sku = sku;
        markAsUpdated();
        validate();
    }

    /**
     * Changes the variant's price.
     */
    public void changePrice(Money newPrice) {
        this.price = newPrice;
        markAsUpdated();
        validate();
    }

    /**
     * Activates the variant.
     */
    public void activate() {
        if (!this.active) {
            this.active = true;
            markAsUpdated();
        }
    }

    /**
     * Deactivates the variant.
     */
    public void deactivate() {
        if (this.active) {
            this.active = false;
            markAsUpdated();
        }
    }

    // Query Methods

    /**
     * Checks if the variant is available for purchase.
     */
    public boolean isAvailable() {
        return active;
    }

    /**
     * Gets a specific attribute value.
     */
    public String getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    /**
     * Checks if the variant has a specific attribute.
     */
    public boolean hasAttribute(String key) {
        return attributes.hasAttribute(key);
    }

    private void validate() {
        if (getId() == null) {
            throw new IllegalArgumentException("Variant ID cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Variant name cannot be null");
        }
        if (sku == null) {
            throw new IllegalArgumentException("Variant SKU cannot be null");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Variant attributes cannot be null");
        }
        if (price == null) {
            throw new IllegalArgumentException("Variant price cannot be null");
        }
        if (!price.isPositive()) {
            throw new IllegalArgumentException("Variant price must be positive");
        }
    }
} 