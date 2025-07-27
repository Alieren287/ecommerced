package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product name.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductName extends ValueObject {

    private final String value;

    public ProductName(String value) {
        this.value = value != null ? value.trim() : null;
        validate();
    }

    /**
     * Creates a ProductName from a string value.
     */
    public static ProductName of(String value) {
        return new ProductName(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product name cannot be null");
        require(!value.trim().isEmpty(), "Product name cannot be empty");
        require(value.length() <= 255, "Product name cannot exceed 255 characters");
        require(value.length() >= 2, "Product name must be at least 2 characters");
    }
} 