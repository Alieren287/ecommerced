package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles adding tags to products.
 */
@Service
@Transactional
public class AddProductTagCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<AddProductTagCommand, Void> {

    public AddProductTagCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public Result<Void> handle(AddProductTagCommand command) {
        try {
            // Find product using base class method
            var productResult = findProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            ProductTag tag = ProductTag.of(command.tag());

            // Add tag
            product.addTag(tag);

            // Save product using base class helper
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to add tag to product: " + e.getMessage());
        }
    }
} 