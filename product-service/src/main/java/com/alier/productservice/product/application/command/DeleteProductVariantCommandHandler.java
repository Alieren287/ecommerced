package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command handler for deleting product variants.
 */
@Service
public class DeleteProductVariantCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<DeleteProductVariantCommand, Void> {

    public DeleteProductVariantCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(DeleteProductVariantCommand command) {
        try {
            // Find the product using base class method
            var productResult = findProductById(command.getProductId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

            // Create value objects
            ProductVariantId variantId = ProductVariantId.of(command.getVariantId());

            // Remove variant
            product.removeVariant(variantId);

            // Save product
            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();

        } catch (Exception e) {
            return Result.failure("Failed to delete product variant: " + e.getMessage());
        }
    }
} 