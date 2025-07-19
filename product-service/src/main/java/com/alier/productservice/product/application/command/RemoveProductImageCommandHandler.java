package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles removing images from products.
 */
@Service
public class RemoveProductImageCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<RemoveProductImageCommand, Void> {

    public RemoveProductImageCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(RemoveProductImageCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.removeImage(command.image().mapToProductImage());

            return saveProductAsVoid(product);
        } catch (Exception e) {
            return Result.failure("Failed to remove product image: " + e.getMessage());
        }
    }
}