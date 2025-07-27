package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

/**
 * Base abstract class for product command handlers.
 * Provides common functionality for finding and validating products.
 */
@RequiredArgsConstructor
public abstract class BaseProductCommandHandler {

    protected final ProductRepository productRepository;

    /**
     * Finds a product by ID with validation.
     *
     * @param productId the product ID as string
     * @return Result containing the Product if found, or failure message if not found or error occurred
     */
    protected Result<Product> getProductById(UUID productId) {
        Result<Optional<Product>> productResult = productRepository.findById(productId);
        if (productResult.isFailure()) {
            return Result.failure(productResult.getError());
        }
        if (productResult.getValue().isEmpty()) {
            return Result.failure("Product not found with ID:" + productId);
        }
        return Result.success(productResult.getValue().get());
    }

    /**
     * Saves a product and converts the result to void.
     * Helper method for handlers that return Result<Void>.
     *
     * @param product the product to save
     * @return Result<Void> indicating success or failure
     */
    protected Result<Void> saveProductAsVoid(Product product) {
        Result<Product> saveResult = productRepository.save(product);
        if (saveResult.isFailure()) {
            return Result.failure(saveResult.getError());
        }
        return Result.success();
    }
}