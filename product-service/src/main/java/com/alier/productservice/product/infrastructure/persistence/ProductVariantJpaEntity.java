package com.alier.productservice.product.infrastructure.persistence;

import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.domain.ProductVariant;
import com.alier.productservice.product.domain.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for ProductVariant persistence.
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_variant_attributes", joinColumns = @JoinColumn(name = "variant_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "inventory_reference_sku", nullable = false, unique = true)
    private String inventoryReferenceSku;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Converts from domain ProductVariant to JPA entity.
     */
    public static ProductVariantJpaEntity fromDomain(ProductVariant variant, ProductJpaEntity product) {
        ProductVariantJpaEntity entity = new ProductVariantJpaEntity();
        entity.setId(variant.getId());
        entity.setProduct(product);
        entity.setName(variant.getName().getValue());
        entity.setSku(variant.getSku().getValue());
        entity.setAttributes(new HashMap<>(variant.getAttributes().getAttributes()));
        entity.setPriceAmount(variant.getPrice().getAmount());
        entity.setPriceCurrency(variant.getPrice().getCurrency().getCurrencyCode());
        entity.setActive(variant.isActive());
        entity.setInventoryReferenceSku(variant.getSku().getValue()); // Use variant SKU as inventory reference
        entity.setCreatedAt(variant.getCreatedAt());
        entity.setUpdatedAt(variant.getUpdatedAt());

        return entity;
    }

    /**
     * Converts from JPA entity to domain ProductVariant.
     */
    public ProductVariant toDomain() {
        return new ProductVariant(
                ProductVariantId.of(this.id),
                ProductId.of(this.product.getId()),
                VariantName.of(this.name),
                VariantSku.of(this.sku),
                VariantAttributes.of(this.attributes),
                new Money(this.priceAmount, Currency.getInstance(this.priceCurrency)),
                this.active,
                this.createdAt,
                this.updatedAt
        );
    }
} 