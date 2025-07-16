package com.alier.productservice.product.domain.valueobject;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product variant SKU (Stock Keeping Unit).
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class VariantSku extends ValueObject {

    private final String value;

    public VariantSku(String value) {
        this.value = value != null ? value.trim().toUpperCase() : null;
        validate();
    }

    /**
     * Creates a VariantSku from a string value.
     */
    public static VariantSku of(String value) {
        return new VariantSku(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Variant SKU cannot be null");
        require(!value.trim().isEmpty(), "Variant SKU cannot be empty");
        require(value.length() <= 50, "Variant SKU cannot exceed 50 characters");
        require(value.length() >= 3, "Variant SKU must be at least 3 characters");
        require(value.matches("^[A-Z0-9\\-_]+$"), "Variant SKU can only contain uppercase letters, numbers, hyphens, and underscores");
    }
} 