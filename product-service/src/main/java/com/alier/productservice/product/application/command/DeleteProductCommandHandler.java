package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the deletion of products.
 */
@Service
@Transactional
public class DeleteProductCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<DeleteProductCommand, Void> {

    public DeleteProductCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public Result<Void> handle(DeleteProductCommand command) {
        try {
            // Find product using base class method
            var productResult = findProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            var product = productResult.getValue();

            // Delete the product
            return productRepository.deleteById(product.getId())
                    .map(result -> null); // Convert Void to null for Result<Void>

        } catch (Exception e) {
            return Result.failure("Failed to delete product: " + e.getMessage());
        }
    }
} 