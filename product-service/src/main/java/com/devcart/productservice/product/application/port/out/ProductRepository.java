package com.devcart.productservice.product.application.port.out;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.out.Repository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductTag;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository interface for Product aggregate.
 */
public interface ProductRepository extends Repository<Product> {

    /**
     * Finds a product by its product ID.
     */
    Result<Optional<Product>> findByProductId(ProductId productId);

    /**
     * Finds all products in a specific category.
     */
    Result<List<Product>> findByCategoryId(UUID categoryId);

    /**
     * Finds all products that contain a specific tag.
     */
    Result<List<Product>> findByTag(ProductTag tag);

    /**
     * Finds all products that contain any of the specified categories.
     */
    Result<List<Product>> findByCategoryIdsIn(Set<UUID> categoryIds);

    /**
     * Finds all products that contain any of the specified tags.
     */
    Result<List<Product>> findByTagsIn(Set<ProductTag> tags);

    /**
     * Finds all active products.
     */
    Result<List<Product>> findAllActive();

    /**
     * Finds products by name containing the given text.
     */
    Result<List<Product>> findByNameContaining(String name);

    /**
     * Counts total number of products.
     */
    Result<Long> countTotal();

    /**
     * Counts active products.
     */
    Result<Long> countActive();
} 