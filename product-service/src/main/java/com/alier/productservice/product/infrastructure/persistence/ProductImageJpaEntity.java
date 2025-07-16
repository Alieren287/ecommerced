package com.alier.productservice.product.infrastructure.persistence;

import com.alier.productservice.product.domain.valueobject.ProductImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA entity for Product Image persistence.
 */
@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "alt_text", length = 500)
    private String altText;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    /**
     * Converts from domain ProductImage to JPA entity.
     */
    public static ProductImageJpaEntity fromDomain(ProductImage productImage, ProductJpaEntity product) {
        ProductImageJpaEntity entity = new ProductImageJpaEntity();
        entity.setUrl(productImage.getUrl());
        entity.setAltText(productImage.getAltText());
        entity.setPrimary(productImage.isPrimary());
        entity.setDisplayOrder(productImage.getDisplayOrder());
        entity.setProduct(product);
        return entity;
    }

    /**
     * Converts from JPA entity to domain ProductImage.
     */
    public ProductImage toDomain() {
        return ProductImage.of(
                this.url,
                this.altText,
                this.isPrimary,
                this.displayOrder
        );
    }
} 