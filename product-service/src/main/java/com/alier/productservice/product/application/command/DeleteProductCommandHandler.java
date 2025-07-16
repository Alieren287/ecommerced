package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the deletion of products.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProductCommandHandler implements CommandHandler<DeleteProductCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    public Result<Void> handle(DeleteProductCommand command) {
        try {
            ProductId productId = ProductId.of(command.productId());

            // Check if product exists
            var existsResult = productRepository.findByProductId(productId);
            if (existsResult.isFailure()) {
                return Result.failure("Failed to check product existence: " + existsResult.getError());
            }

            if (existsResult.getValue().isEmpty()) {
                return Result.failure("Product not found with ID: " + command.productId());
            }

            // Get the product's entity ID for deletion
            var product = existsResult.getValue().get();

            // Delete the product
            return productRepository.deleteById(product.getId())
                    .map(result -> null); // Convert Void to null for Result<Void>

        } catch (Exception e) {
            return Result.failure("Failed to delete product: " + e.getMessage());
        }
    }
} 