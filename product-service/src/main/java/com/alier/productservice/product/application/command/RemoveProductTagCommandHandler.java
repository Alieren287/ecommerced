package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles removing tags from products.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RemoveProductTagCommandHandler implements CommandHandler<RemoveProductTagCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    public Result<Void> handle(RemoveProductTagCommand command) {
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
            ProductTag tag = ProductTag.of(command.tag());

            // Remove tag
            product.removeTag(tag);

            // Save product
            return productRepository.save(product)
                    .map(savedProduct -> null); // Convert to Result<Void>

        } catch (Exception e) {
            return Result.failure("Failed to remove tag from product: " + e.getMessage());
        }
    }
} 