package com.alier.productservice.product.domain;

import com.alier.ecommerced.core.domain.common.AggregateRoot;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.domain.event.*;
import com.alier.productservice.product.domain.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Product aggregate root representing a sellable product in the catalog.
 */
@Getter
public class Product extends AggregateRoot {

    private ProductName name;
    private ProductDescription description;
    private ProductSlug slug;
    private ProductAttributes attributes;
    private Set<UUID> categoryIds;
    private Set<ProductTag> tags;
    private List<ProductImage> images;
    private final List<ProductVariant> variants;
    private boolean active;

    // For creating new products
    public Product(ProductName name, ProductDescription description, ProductSlug slug,
                   ProductAttributes attributes, Set<UUID> categoryIds) {
        super(); // Generate entity ID
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.attributes = attributes != null ? attributes : ProductAttributes.empty();
        this.categoryIds = new HashSet<>(categoryIds != null ? categoryIds : Collections.emptySet());
        this.tags = new HashSet<>();
        this.images = new ArrayList<>();
        this.variants = new ArrayList<>();
        this.active = true;

        validate();
    }

    // For reconstructing from persistence
    public Product(UUID productId, ProductName name, ProductDescription description,
                   ProductSlug slug, ProductAttributes attributes, Set<UUID> categoryIds,
                   Set<ProductTag> tags, List<ProductImage> images, List<ProductVariant> variants,
                   boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(productId, createdAt, updatedAt);
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.attributes = attributes != null ? attributes : ProductAttributes.empty();
        this.categoryIds = new HashSet<>(categoryIds != null ? categoryIds : Collections.emptySet());
        this.tags = new HashSet<>(tags != null ? tags : Collections.emptySet());
        this.images = new ArrayList<>(images != null ? images : Collections.emptyList());
        this.variants = new ArrayList<>(variants != null ? variants : Collections.emptyList());
        this.active = active;

        validate();
    }

    // Basic Product Operations

    /**
     * Updates the product's basic information.
     */
    public void updateBasicInfo(ProductName name, ProductDescription description,
                                ProductSlug slug, ProductAttributes attributes) {
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.attributes = attributes != null ? attributes : ProductAttributes.empty();

        markAsUpdated();
        validate();
        publishEvent(new ProductUpdatedEvent(ProductId.of(getId()), this.name));
    }

    /**
     * Activates the product for sale.
     */
    public void activate() {
        businessRequire(!this.active, "Product is already active");

        this.active = true;
        markAsUpdated();
        publishEvent(new ProductActivatedEvent(ProductId.of(getId())));
    }

    /**
     * Deactivates the product from sale.
     */
    public void deactivate() {
        businessRequire(this.active, "Product is already inactive");

        this.active = false;
        markAsUpdated();
        publishEvent(new ProductDeactivatedEvent(ProductId.of(getId())));
    }

    // Variant Management

    /**
     * Adds a new variant to the product.
     */
    public void addVariant(VariantName name, VariantSku sku, VariantAttributes attributes, Money price) {
        businessRequire(name != null, "Variant name cannot be null");
        businessRequire(sku != null, "Variant SKU cannot be null");
        businessRequire(attributes != null, "Variant attributes cannot be null");
        businessRequire(price != null, "Variant price cannot be null");

        // Check for duplicate SKU
        businessRequire(!hasVariantWithSku(sku), "A variant with SKU " + sku.getValue() + " already exists");

        ProductVariant variant = new ProductVariant(ProductId.of(getId()), name, sku, attributes, price);
        this.variants.add(variant);

        markAsUpdated();
        publishEvent(new ProductVariantCreatedEvent(ProductId.of(getId()), ProductVariantId.of(variant.getId()), name, sku));
    }

    /**
     * Updates an existing variant.
     */
    public void updateVariant(ProductVariantId variantId, VariantName name, VariantAttributes attributes) {
        businessRequire(variantId != null, "Variant ID cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        variant.updateBasicInfo(name, attributes);

        markAsUpdated();
        publishEvent(new ProductVariantUpdatedEvent(ProductId.of(getId()), variantId, name));
    }

    /**
     * Updates a variant's SKU.
     */
    public void updateVariantSku(ProductVariantId variantId, VariantSku newSku) {
        businessRequire(variantId != null, "Variant ID cannot be null");
        businessRequire(newSku != null, "Variant SKU cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        // Check for duplicate SKU (excluding current variant)
        businessRequire(!hasVariantWithSkuExcluding(newSku, variantId),
                "A variant with SKU " + newSku.getValue() + " already exists");

        variant.updateSku(newSku);
        markAsUpdated();
    }

    /**
     * Changes a variant's price.
     */
    public void changeVariantPrice(ProductVariantId variantId, Money newPrice) {
        businessRequire(variantId != null, "Variant ID cannot be null");
        businessRequire(newPrice != null, "Variant price cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        variant.changePrice(newPrice);
        markAsUpdated();
    }

    /**
     * Activates a variant.
     */
    public void activateVariant(ProductVariantId variantId) {
        businessRequire(variantId != null, "Variant ID cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        variant.activate();
        markAsUpdated();
    }

    /**
     * Deactivates a variant.
     */
    public void deactivateVariant(ProductVariantId variantId) {
        businessRequire(variantId != null, "Variant ID cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        variant.deactivate();
        markAsUpdated();
    }

    /**
     * Removes a variant from the product.
     */
    public void removeVariant(ProductVariantId variantId) {
        businessRequire(variantId != null, "Variant ID cannot be null");

        ProductVariant variant = findVariantById(variantId);
        businessRequire(variant != null, "Variant not found: " + variantId.getValue());

        this.variants.remove(variant);

        markAsUpdated();
        publishEvent(new ProductVariantDeletedEvent(ProductId.of(getId()), variantId));
    }

    // Category Management

    /**
     * Adds a category to the product.
     */
    public void addCategory(UUID categoryId) {
        businessRequire(categoryId != null, "Category ID cannot be null");
        businessRequire(!this.categoryIds.contains(categoryId), "Category already exists: " + categoryId);

        this.categoryIds.add(categoryId);
        markAsUpdated();
        publishEvent(new ProductCategoryAddedEvent(ProductId.of(getId()), categoryId));
    }

    /**
     * Removes a category from the product.
     */
    public void removeCategory(UUID categoryId) {
        businessRequire(categoryId != null, "Category ID cannot be null");
        businessRequire(this.categoryIds.contains(categoryId), "Category does not exist: " + categoryId);
        businessRequire(this.categoryIds.size() > 1, "Cannot remove the last category. Product must have at least one category");

        this.categoryIds.remove(categoryId);
        markAsUpdated();
        publishEvent(new ProductCategoryRemovedEvent(ProductId.of(getId()), categoryId));
    }

    /**
     * Updates all categories for the product.
     */
    public void updateCategories(Set<UUID> newCategoryIds) {
        businessRequire(newCategoryIds != null && !newCategoryIds.isEmpty(), "Product must have at least one category");

        Set<UUID> oldCategoryIds = new HashSet<>(this.categoryIds);
        this.categoryIds = new HashSet<>(newCategoryIds);

        markAsUpdated();
        validate();
        publishEvent(new ProductCategoriesUpdatedEvent(ProductId.of(getId()), oldCategoryIds, this.categoryIds));
    }

    // Tag Management

    /**
     * Adds a tag to the product.
     */
    public void addTag(ProductTag tag) {
        businessRequire(tag != null, "Tag cannot be null");
        businessRequire(!this.tags.contains(tag), "Tag already exists: " + tag.getValue());

        this.tags.add(tag);
        markAsUpdated();
        publishEvent(new ProductTagAddedEvent(ProductId.of(getId()), tag));
    }

    /**
     * Removes a tag from the product.
     */
    public void removeTag(ProductTag tag) {
        businessRequire(tag != null, "Tag cannot be null");
        businessRequire(this.tags.contains(tag), "Tag does not exist: " + tag.getValue());

        this.tags.remove(tag);
        markAsUpdated();
        publishEvent(new ProductTagRemovedEvent(ProductId.of(getId()), tag));
    }

    /**
     * Updates all tags for the product.
     */
    public void updateTags(Set<ProductTag> newTags) {
        Set<ProductTag> oldTags = new HashSet<>(this.tags);
        this.tags = new HashSet<>(newTags != null ? newTags : Collections.emptySet());

        markAsUpdated();
        publishEvent(new ProductTagsUpdatedEvent(ProductId.of(getId()), oldTags, this.tags));
    }

    // Image Management

    /**
     * Adds an image to the product.
     */
    public void addImage(ProductImage image) {
        businessRequire(image != null, "Image cannot be null");
        businessRequire(!this.images.contains(image), "Image already exists");

        // If this is a primary image, ensure no other primary images exist
        if (image.isPrimary()) {
            businessRequire(getPrimaryImage().isEmpty(), "Product already has a primary image");
        }

        this.images.add(image);
        // Sort images by display order and primary status
        this.images.sort((a, b) -> {
            if (a.isPrimary() != b.isPrimary()) {
                return a.isPrimary() ? -1 : 1;
            }
            return Integer.compare(a.getDisplayOrder(), b.getDisplayOrder());
        });

        markAsUpdated();
        publishEvent(new ProductImageAddedEvent(ProductId.of(getId()), image));
    }

    /**
     * Removes an image from the product.
     */
    public void removeImage(ProductImage image) {
        businessRequire(image != null, "Image cannot be null");
        businessRequire(this.images.contains(image), "Image does not exist");

        this.images.remove(image);
        markAsUpdated();
        publishEvent(new ProductImageRemovedEvent(ProductId.of(getId()), image));
    }

    /**
     * Updates all images for the product.
     */
    public void updateImages(List<ProductImage> newImages) {
        List<ProductImage> oldImages = new ArrayList<>(this.images);
        this.images = new ArrayList<>(newImages != null ? newImages : Collections.emptyList());

        // Validate that there's at most one primary image
        long primaryCount = this.images.stream().filter(ProductImage::isPrimary).count();
        businessRequire(primaryCount <= 1, "Product cannot have more than one primary image");

        // Sort images
        this.images.sort((a, b) -> {
            if (a.isPrimary() != b.isPrimary()) {
                return a.isPrimary() ? -1 : 1;
            }
            return Integer.compare(a.getDisplayOrder(), b.getDisplayOrder());
        });

        markAsUpdated();
        publishEvent(new ProductImagesUpdatedEvent(ProductId.of(getId()), oldImages, this.images));
    }

    // Query Methods

    /**
     * Gets the primary image if it exists.
     */
    public Optional<ProductImage> getPrimaryImage() {
        return this.images.stream()
                .filter(ProductImage::isPrimary)
                .findFirst();
    }

    /**
     * Gets all secondary images ordered by display order.
     */
    public List<ProductImage> getSecondaryImages() {
        return this.images.stream()
                .filter(img -> !img.isPrimary())
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the product belongs to a specific category.
     */
    public boolean hasCategory(UUID categoryId) {
        return this.categoryIds.contains(categoryId);
    }

    /**
     * Checks if the product has a specific tag.
     */
    public boolean hasTag(ProductTag tag) {
        return this.tags.contains(tag);
    }

    /**
     * Checks if the product is available for purchase.
     */
    public boolean isAvailable() {
        return active && hasAvailableVariants();
    }

    /**
     * Gets a specific attribute value.
     */
    public String getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    /**
     * Checks if the product has a specific attribute.
     */
    public boolean hasAttribute(String key) {
        return attributes.hasAttribute(key);
    }

    /**
     * Finds a variant by its ID.
     */
    public ProductVariant findVariantById(ProductVariantId variantId) {
        return this.variants.stream()
                .filter(v -> ProductVariantId.of(v.getId()).equals(variantId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a variant by its SKU.
     */
    public Optional<ProductVariant> findVariantBySku(VariantSku sku) {
        return this.variants.stream()
                .filter(v -> v.getSku().equals(sku))
                .findFirst();
    }

    /**
     * Gets all active variants.
     */
    public List<ProductVariant> getActiveVariants() {
        return this.variants.stream()
                .filter(ProductVariant::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Gets all available variants (active and in stock).
     */
    public List<ProductVariant> getAvailableVariants() {
        return this.variants.stream()
                .filter(ProductVariant::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the product has variants.
     */
    public boolean hasVariants() {
        return !this.variants.isEmpty();
    }

    /**
     * Checks if the product has available variants.
     */
    public boolean hasAvailableVariants() {
        return this.variants.stream().anyMatch(ProductVariant::isAvailable);
    }

    /**
     * Checks if a variant with the given SKU exists.
     */
    public boolean hasVariantWithSku(VariantSku sku) {
        return this.variants.stream()
                .anyMatch(v -> v.getSku().equals(sku));
    }

    /**
     * Checks if a variant with the given SKU exists, excluding a specific variant.
     */
    public boolean hasVariantWithSkuExcluding(VariantSku sku, ProductVariantId excludeVariantId) {
        return this.variants.stream()
                .filter(v -> !ProductVariantId.of(v.getId()).equals(excludeVariantId))
                .anyMatch(v -> v.getSku().equals(sku));
    }

    @Override
    protected void validate() {
        businessRequire(getId() != null, "Product ID cannot be null");
        businessRequire(name != null, "Product name cannot be null");
        businessRequire(description != null, "Product description cannot be null");
        businessRequire(slug != null, "Product slug cannot be null");
        businessRequire(attributes != null, "Product attributes cannot be null");
        businessRequire(categoryIds != null && !categoryIds.isEmpty(), "Product must have at least one category");

        // Validate that there's at most one primary image
        if (images != null) {
            long primaryCount = images.stream().filter(ProductImage::isPrimary).count();
            businessRequire(primaryCount <= 1, "Product cannot have more than one primary image");
        }

        // Validate variants
        if (variants != null) {
            // Check for duplicate SKUs
            Set<VariantSku> skus = new HashSet<>();
            for (ProductVariant variant : variants) {
                businessRequire(!skus.contains(variant.getSku()), "Duplicate variant SKU: " + variant.getSku().getValue());
                skus.add(variant.getSku());
            }
        }
    }
} 