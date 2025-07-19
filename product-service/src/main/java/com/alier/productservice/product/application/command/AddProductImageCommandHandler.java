package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductImage;
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
    public Result<Void> handle(AddProductImageCommand command) {
        try {
            // Find product using base class method
            var productResult = findProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            ProductImage image = ProductImage.of(command.url(), command.altText(),
                    command.isPrimary(), command.displayOrder());

            // Add image
            product.addImage(image);

            // Save product using base class helper
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to add image to product: " + e.getMessage());
        }
    }
} 