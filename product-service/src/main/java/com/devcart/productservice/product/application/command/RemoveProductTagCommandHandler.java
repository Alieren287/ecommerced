package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.ProductTag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles removing tags from products.
 */
@Service
@Transactional
public class RemoveProductTagCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<RemoveProductTagCommand, Void> {

    public RemoveProductTagCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public Result<Void> handle(RemoveProductTagCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            ProductTag tag = ProductTag.of(command.tag());

            // Remove tag
            product.removeTag(tag);

            // Save product using base class helper
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to remove tag from product: " + e.getMessage());
        }
    }
} 