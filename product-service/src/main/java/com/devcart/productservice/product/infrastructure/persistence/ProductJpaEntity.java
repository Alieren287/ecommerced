package com.devcart.productservice.product.infrastructure.persistence;

import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.ProductVariant;
import com.devcart.productservice.product.domain.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JPA entity for Product persistence.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    // Optional: for brand, could be separate entity/service if complex, or just string
    @Column(name = "brand")
    private String brand;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category_id")
    private Set<UUID> categoryIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<ProductImageJpaEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("sku ASC")
    private List<ProductVariantJpaEntity> variants = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Converts from domain Product to JPA entity.
     */
    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setName(product.getName().getValue());
        entity.setDescription(product.getDescription().getValue());
        entity.setSlug(product.getSlug().getValue());
        entity.setAttributes(new HashMap<>(product.getAttributes().getAttributes()));

        // Convert category IDs
        entity.setCategoryIds(new HashSet<>(product.getCategoryIds()));

        // Convert tags
        entity.setTags(product.getTags().stream()
                .map(ProductTag::getValue)
                .collect(Collectors.toSet()));

        // Convert images
        entity.setImages(product.getImages().stream()
                .map(image -> ProductImageJpaEntity.fromDomain(image, entity))
                .collect(Collectors.toList()));

        // Convert variants
        entity.setVariants(product.getVariants().stream()
                .map(variant -> ProductVariantJpaEntity.fromDomain(variant, entity))
                .collect(Collectors.toList()));

        entity.setActive(product.isActive());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());

        return entity;
    }

    /**
     * Converts from JPA entity to domain Product.
     */
    public Product toDomain() {
        // Convert tags
        Set<ProductTag> domainTags = tags.stream()
                .map(ProductTag::of)
                .collect(Collectors.toSet());

        // Convert images
        List<ProductImage> domainImages = images.stream()
                .map(ProductImageJpaEntity::toDomain)
                .collect(Collectors.toList());

        // Convert variants
        List<ProductVariant> domainVariants = variants.stream()
                .map(ProductVariantJpaEntity::toDomain)
                .collect(Collectors.toList());

        return new Product(
                this.id,
                ProductName.of(this.name),
                ProductDescription.of(this.description),
                ProductSlug.of(this.slug),
                ProductAttributes.of(this.attributes),
                new HashSet<>(this.categoryIds),
                domainTags,
                domainImages,
                domainVariants,
                this.active,
                this.createdAt,
                this.updatedAt
        );
    }
} 