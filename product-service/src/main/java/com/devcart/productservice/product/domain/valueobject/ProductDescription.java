package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product description.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductDescription extends ValueObject {

    private final String value;

    public ProductDescription(String value) {
        this.value = value != null ? value.trim() : null;
        validate();
    }

    /**
     * Creates a ProductDescription from a string value.
     */
    public static ProductDescription of(String value) {
        return new ProductDescription(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product description cannot be null");
        require(!value.trim().isEmpty(), "Product description cannot be empty");
        require(value.length() <= 2000, "Product description cannot exceed 2000 characters");
        require(value.length() >= 10, "Product description must be at least 10 characters");
    }
} 