package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles adding images to products.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddProductImageCommandHandler implements CommandHandler<AddProductImageCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    public Result<Void> handle(AddProductImageCommand command) {
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
            ProductImage image = ProductImage.of(command.url(), command.altText(),
                    command.isPrimary(), command.displayOrder());

            // Add image
            product.addImage(image);

            // Save product
            return productRepository.save(product)
                    .map(savedProduct -> null); // Convert to Result<Void>

        } catch (Exception e) {
            return Result.failure("Failed to add image to product: " + e.getMessage());
        }
    }
} 