package com.alier.productservice.product.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for ProductJpaEntity.
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

    // findByProductId is now equivalent to findById since we use the same ID for both

    /**
     * Finds all products that contain a specific category.
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE :categoryId MEMBER OF p.categoryIds")
    List<ProductJpaEntity> findByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Finds all products that contain a specific tag.
     */
    @Query("SELECT p FROM ProductJpaEntity p WHERE :tag MEMBER OF p.tags")
    List<ProductJpaEntity> findByTag(@Param("tag") String tag);

    /**
     * Finds all products that contain any of the specified categories.
     */
    @Query("SELECT DISTINCT p FROM ProductJpaEntity p WHERE EXISTS (SELECT 1 FROM p.categoryIds c WHERE c IN :categoryIds)")
    List<ProductJpaEntity> findByCategoryIdsIn(@Param("categoryIds") List<UUID> categoryIds);

    /**
     * Finds all products that contain any of the specified tags.
     */
    @Query("SELECT DISTINCT p FROM ProductJpaEntity p WHERE EXISTS (SELECT 1 FROM p.tags t WHERE t IN :tags)")
    List<ProductJpaEntity> findByTagsIn(@Param("tags") List<String> tags);

    /**
     * Finds all active products.
     */
    List<ProductJpaEntity> findByActiveTrue();

    /**
     * Finds products by name containing the given text (case-insensitive).
     */
    List<ProductJpaEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Counts active products.
     */
    @Query("SELECT COUNT(p) FROM ProductJpaEntity p WHERE p.active = true")
    long countActiveProducts();

    /**
     * Checks if a product exists by its product ID.
     */
    boolean existsByProductId(UUID productId);
} 