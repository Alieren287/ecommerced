package com.alier.productservice.product.domain.valueobject;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * Value object representing a unique product variant identifier.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductVariantId extends ValueObject {

    private final UUID value;

    public ProductVariantId(UUID value) {
        this.value = value;
        validate();
    }

    public ProductVariantId(String value) {
        this.value = UUID.fromString(value);
        validate();
    }

    /**
     * Generates a new unique ProductVariantId.
     */
    public static ProductVariantId generate() {
        return new ProductVariantId(UUID.randomUUID());
    }

    /**
     * Creates a ProductVariantId from a string value.
     */
    public static ProductVariantId of(String value) {
        return new ProductVariantId(value);
    }

    /**
     * Creates a ProductVariantId from a UUID value.
     */
    public static ProductVariantId of(UUID value) {
        return new ProductVariantId(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product variant ID cannot be null");
    }
} 