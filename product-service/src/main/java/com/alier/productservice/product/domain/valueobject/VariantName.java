package com.alier.productservice.product.domain.valueobject;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product variant name.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class VariantName extends ValueObject {

    private final String value;

    public VariantName(String value) {
        this.value = value != null ? value.trim() : null;
        validate();
    }

    /**
     * Creates a VariantName from a string value.
     */
    public static VariantName of(String value) {
        return new VariantName(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Variant name cannot be null");
        require(!value.trim().isEmpty(), "Variant name cannot be empty");
        require(value.length() <= 255, "Variant name cannot exceed 255 characters");
        require(value.length() >= 2, "Variant name must be at least 2 characters");
    }
} 