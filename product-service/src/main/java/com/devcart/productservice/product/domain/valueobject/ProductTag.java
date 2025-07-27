package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product tag.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductTag extends ValueObject {

    private final String value;

    public ProductTag(String value) {
        this.value = value != null ? value.trim().toLowerCase() : null;
        validate();
    }

    /**
     * Creates a ProductTag from a string value.
     */
    public static ProductTag of(String value) {
        return new ProductTag(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product tag cannot be null");
        require(!value.trim().isEmpty(), "Product tag cannot be empty");
        require(value.length() <= 50, "Product tag cannot exceed 50 characters");
        require(value.length() >= 2, "Product tag must be at least 2 characters");
        require(value.matches("^[a-z0-9-_]+$"), "Product tag must contain only lowercase letters, numbers, hyphens, and underscores");
    }
} 