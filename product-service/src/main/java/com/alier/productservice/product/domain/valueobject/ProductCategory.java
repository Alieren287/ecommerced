package com.alier.productservice.product.domain.valueobject;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product category.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductCategory extends ValueObject {

    // Common categories
    public static final ProductCategory ELECTRONICS = new ProductCategory("ELECTRONICS");
    public static final ProductCategory CLOTHING = new ProductCategory("CLOTHING");
    public static final ProductCategory BOOKS = new ProductCategory("BOOKS");
    public static final ProductCategory HOME_GARDEN = new ProductCategory("HOME_GARDEN");
    public static final ProductCategory SPORTS = new ProductCategory("SPORTS");
    public static final ProductCategory HEALTH_BEAUTY = new ProductCategory("HEALTH_BEAUTY");
    private final String value;
    public ProductCategory(String value) {
        this.value = value != null ? value.trim().toUpperCase() : null;
        validate();
    }

    /**
     * Creates a ProductCategory from a string value.
     */
    public static ProductCategory of(String value) {
        return new ProductCategory(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product category cannot be null");
        require(!value.trim().isEmpty(), "Product category cannot be empty");
        require(value.length() <= 100, "Product category cannot exceed 100 characters");
        require(value.length() >= 2, "Product category must be at least 2 characters");
        require(value.matches("^[A-Z_]+$"), "Product category must contain only uppercase letters and underscores");
    }
} 