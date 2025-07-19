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
@Transactional
public class RemoveProductImageCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<RemoveProductImageCommand, Void> {

    public RemoveProductImageCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public Result<Void> handle(RemoveProductImageCommand command) {
        try {
            // Find product using base class method
            var productResult = findProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

            // Find the image to remove
            var imageToRemove = product.getImages().stream()
                    .filter(img -> img.getUrl().equals(command.imageUrl()))
                    .findFirst();

            if (imageToRemove.isEmpty()) {
                return Result.failure("Image not found with URL: " + command.imageUrl());
            }

            // Remove image
            product.removeImage(imageToRemove.get());

            // Save product using base class helper
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to remove image from product: " + e.getMessage());
        }
    }
} 