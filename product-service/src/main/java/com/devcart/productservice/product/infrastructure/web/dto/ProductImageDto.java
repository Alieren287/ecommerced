package com.devcart.productservice.product.infrastructure.web.dto;

import com.devcart.productservice.product.domain.valueobject.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product images in web requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {

    private String url;
    private String altText;
    private boolean isPrimary;
    private int displayOrder;

    /**
     * Maps a ProductImageDto to a ProductImage domain object.
     */
    public ProductImage mapToProductImage() {
        return ProductImage.of(url, altText, isPrimary, displayOrder);
    }
} 