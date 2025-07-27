package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command handler for deleting products.
 * Follows proper DDD pattern by loading the aggregate, calling domain method, and persisting events.
 */
@Service
public class DeleteProductCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<DeleteProductCommand, Void> {

    public DeleteProductCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(DeleteProductCommand command) {
        try {
            // Find the product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

            // Call domain method to mark for deletion (publishes ProductDeletedEvent)
            product.delete();

            // Save product to persist the domain event via outbox pattern
            // Note: The actual deletion from database could be handled by a separate process
            // that listens to ProductDeletedEvent, or we can delete here after event publishing
            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            // Perform actual database deletion after event is persisted
            return productRepository.deleteById(command.productId());

        } catch (Exception e) {
            return Result.failure("Failed to delete product: " + e.getMessage());
        }
    }
}