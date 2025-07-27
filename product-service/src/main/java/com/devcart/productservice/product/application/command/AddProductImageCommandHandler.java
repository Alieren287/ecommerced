package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles adding images to products.
 */
@Service
@Transactional
public class AddProductImageCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<AddProductImageCommand, Void> {

    public AddProductImageCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(AddProductImageCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.addImage(command.image().mapToProductImage());

            return saveProductAsVoid(product);
        } catch (Exception e) {
            return Result.failure("Failed to add product image: " + e.getMessage());
        }
    }
}