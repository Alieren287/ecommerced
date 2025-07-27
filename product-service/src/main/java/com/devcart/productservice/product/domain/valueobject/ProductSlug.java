package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product slug (URL-friendly identifier).
 * This is used for SEO-friendly URLs and product identification.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductSlug extends ValueObject {

    private final String value;

    public ProductSlug(String value) {
        this.value = value != null ? value.trim().toLowerCase() : null;
        validate();
    }

    /**
     * Creates a ProductSlug from a string value.
     */
    public static ProductSlug of(String value) {
        return new ProductSlug(value);
    }

    /**
     * Creates a ProductSlug from a product name by converting it to a slug format.
     */
    public static ProductSlug fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        String slug = name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters except spaces and hyphens
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single hyphen
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens

        return new ProductSlug(slug);
    }

    @Override
    protected void validate() {
        require(value != null, "Product slug cannot be null");
        require(!value.trim().isEmpty(), "Product slug cannot be empty");
        require(value.length() <= 100, "Product slug cannot exceed 100 characters");
        require(value.length() >= 2, "Product slug must be at least 2 characters");
        require(value.matches("^[a-z0-9\\-]+$"), "Product slug can only contain lowercase letters, numbers, and hyphens");
        require(!value.startsWith("-") && !value.endsWith("-"), "Product slug cannot start or end with hyphen");
        require(!value.contains("--"), "Product slug cannot contain consecutive hyphens");
    }
} 