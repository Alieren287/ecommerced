package com.alier.productservice.product.infrastructure.persistence;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of ProductRepository.
 * This adapter connects the domain layer to the persistence layer.
 */
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Result<Product> save(Product product) {
        try {
            ProductJpaEntity entity = ProductJpaEntity.fromDomain(product);
            ProductJpaEntity saved = jpaRepository.save(entity);
            return Result.success(saved.toDomain());
        } catch (Exception e) {
            return Result.failure("Failed to save product: " + e.getMessage());
        }
    }

    @Override
    public Result<Optional<Product>> findById(UUID id) {
        try {
            Optional<ProductJpaEntity> entity = jpaRepository.findById(id);
            Optional<Product> product = entity.map(ProductJpaEntity::toDomain);
            return Result.success(product);
        } catch (Exception e) {
            return Result.failure("Failed to find product by ID: " + e.getMessage());
        }
    }

    @Override
    public Result<Optional<Product>> findByProductId(ProductId productId) {
        try {
            Optional<ProductJpaEntity> entity = jpaRepository.findById(productId.getValue());
            Optional<Product> product = entity.map(ProductJpaEntity::toDomain);
            return Result.success(product);
        } catch (Exception e) {
            return Result.failure("Failed to find product by product ID: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Product>> findByCategoryId(UUID categoryId) {
        try {
            List<ProductJpaEntity> entities = jpaRepository.findByCategoryId(categoryId);
            List<Product> products = entities.stream()
                    .map(ProductJpaEntity::toDomain)
                    .collect(Collectors.toList());
            return Result.success(products);
        } catch (Exception e) {
            return Result.failure("Failed to find products by tag: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Product>> findByTag(ProductTag tag) {
        try {
            List<ProductJpaEntity> entities = jpaRepository.findByTag(tag.getValue());
            List<Product> products = entities.stream()
                    .map(ProductJpaEntity::toDomain)
                    .collect(Collectors.toList());
            return Result.success(products);
        } catch (Exception e) {
            return Result.failure("Failed to find products by tag: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Product>> findByCategoryIdsIn(Set<UUID> categoryIds) {
        return null;
    }

    @Override
    public Result<List<Product>> findByTagsIn(Set<ProductTag> tags) {
        try {
            List<String> tagValues = tags.stream()
                    .map(ProductTag::getValue)
                    .collect(Collectors.toList());
            List<ProductJpaEntity> entities = jpaRepository.findByTagsIn(tagValues);
            List<Product> products = entities.stream()
                    .map(ProductJpaEntity::toDomain)
                    .collect(Collectors.toList());
            return Result.success(products);
        } catch (Exception e) {
            return Result.failure("Failed to find products by tags: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Product>> findAllActive() {
        try {
            List<ProductJpaEntity> entities = jpaRepository.findByActiveTrue();
            List<Product> products = entities.stream()
                    .map(ProductJpaEntity::toDomain)
                    .collect(Collectors.toList());
            return Result.success(products);
        } catch (Exception e) {
            return Result.failure("Failed to find active products: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Product>> findByNameContaining(String name) {
        try {
            List<ProductJpaEntity> entities = jpaRepository.findByNameContainingIgnoreCase(name);
            List<Product> products = entities.stream()
                    .map(ProductJpaEntity::toDomain)
                    .collect(Collectors.toList());
            return Result.success(products);
        } catch (Exception e) {
            return Result.failure("Failed to find products by name: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countTotal() {
        try {
            long count = jpaRepository.count();
            return Result.success(count);
        } catch (Exception e) {
            return Result.failure("Failed to count total products: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countActive() {
        try {
            long count = jpaRepository.countActiveProducts();
            return Result.success(count);
        } catch (Exception e) {
            return Result.failure("Failed to count active products: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteById(UUID id) {
        try {
            if (jpaRepository.existsById(id)) {
                jpaRepository.deleteById(id);
                return Result.success(null);
            } else {
                return Result.failure("Product not found with ID: " + id);
            }
        } catch (Exception e) {
            return Result.failure("Failed to delete product: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> existsById(UUID id) {
        try {
            boolean exists = jpaRepository.existsById(id);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.failure("Failed to check product existence: " + e.getMessage());
        }
    }
} 