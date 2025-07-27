package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a product image.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductImage extends ValueObject {

    private final String url;
    private final String altText;
    private final boolean isPrimary;
    private final int displayOrder;

    public ProductImage(String url, String altText, boolean isPrimary, int displayOrder) {
        this.url = url != null ? url.trim() : null;
        this.altText = altText != null ? altText.trim() : null;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
        validate();
    }

    /**
     * Creates a primary ProductImage.
     */
    public static ProductImage createPrimary(String url, String altText) {
        return new ProductImage(url, altText, true, 0);
    }

    /**
     * Creates a secondary ProductImage.
     */
    public static ProductImage createSecondary(String url, String altText, int displayOrder) {
        return new ProductImage(url, altText, false, displayOrder);
    }

    /**
     * Creates a ProductImage with explicit parameters.
     */
    public static ProductImage of(String url, String altText, boolean isPrimary, int displayOrder) {
        return new ProductImage(url, altText, isPrimary, displayOrder);
    }

    @Override
    protected void validate() {
        require(url != null, "Product image URL cannot be null");
        require(!url.trim().isEmpty(), "Product image URL cannot be empty");
        require(url.length() <= 500, "Product image URL cannot exceed 500 characters");
        require(isValidUrl(url), "Product image URL must be a valid URL");
        require(altText != null, "Product image alt text cannot be null");
        require(altText.length() <= 255, "Product image alt text cannot exceed 255 characters");
        require(displayOrder >= 0, "Product image display order must be non-negative");
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 