package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles removing images from products.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RemoveProductImageCommandHandler implements CommandHandler<RemoveProductImageCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    public Result<Void> handle(RemoveProductImageCommand command) {
        try {
            ProductId productId = ProductId.of(command.productId());

            // Find product
            var productResult = productRepository.findByProductId(productId);
            if (productResult.isFailure()) {
                return Result.failure("Failed to find product: " + productResult.getError());
            }

            var productOptional = productResult.getValue();
            if (productOptional.isEmpty()) {
                return Result.failure("Product not found with ID: " + command.productId());
            }

            Product product = productOptional.get();

            // Find the image to remove
            var imageToRemove = product.getImages().stream()
                    .filter(img -> img.getUrl().equals(command.imageUrl()))
                    .findFirst();

            if (imageToRemove.isEmpty()) {
                return Result.failure("Image not found with URL: " + command.imageUrl());
            }

            // Remove image
            product.removeImage(imageToRemove.get());

            // Save product
            return productRepository.save(product)
                    .map(savedProduct -> null); // Convert to Result<Void>

        } catch (Exception e) {
            return Result.failure("Failed to remove image from product: " + e.getMessage());
        }
    }
} 