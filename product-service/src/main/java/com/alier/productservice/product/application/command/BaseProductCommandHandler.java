package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.RequiredArgsConstructor;

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
    protected Result<Product> findProductById(UUID productId) {
        try {
            ProductId id = ProductId.of(productId);

            // Find product
            var productResult = productRepository.findByProductId(id);
            if (productResult.isFailure()) {
                return Result.failure("Failed to find product: " + productResult.getError());
            }

            var productOptional = productResult.getValue();
            return productOptional.map(Result::success).orElseGet(
                    () -> Result.failure("Product not found with ID: " + productId));

        } catch (Exception e) {
            return Result.failure("Error processing product ID: " + e.getMessage());
        }
    }

    /**
     * Saves a product and converts the result to void.
     * Helper method for handlers that return Result<Void>.
     *
     * @param product the product to save
     * @return Result<Void> indicating success or failure
     */
    protected Result<Void> saveProductAsVoid(Product product) {
        return productRepository.save(product)
                .map(_ -> null); // Convert to Result<Void>
    }
} 