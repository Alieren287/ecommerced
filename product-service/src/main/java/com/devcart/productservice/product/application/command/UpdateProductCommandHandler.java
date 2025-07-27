package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.*;
import com.devcart.productservice.product.infrastructure.web.dto.ProductImageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the updating of existing products.
 */
@Service
@Transactional
public class UpdateProductCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<UpdateProductCommand, Void> {

    public UpdateProductCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public Result<Void> handle(UpdateProductCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

            // Update basic info if provided
            if (command.name() != null && command.description() != null && command.slug() != null) {
                ProductName name = ProductName.of(command.name());
                ProductDescription description = ProductDescription.of(command.description());
                ProductSlug slug = ProductSlug.of(command.slug());
                ProductAttributes attributes = ProductAttributes.of(command.attributes());
                product.updateBasicInfo(name, description, slug, attributes);
            }

            // Update categories if provided
            if (command.categoryIds() != null) {
                product.updateCategories(command.categoryIds());
            }

            // Update tags if provided
            if (command.tags() != null) {
                Set<ProductTag> tags = command.tags().stream()
                        .map(ProductTag::of)
                        .collect(Collectors.toSet());
                product.updateTags(tags);
            }

            // Update images if provided
            if (command.images() != null) {
                List<ProductImage> images = command.images().stream()
                        .map(ProductImageDto::mapToProductImage)
                        .collect(Collectors.toList());
                product.updateImages(images);
            }

            // Update active status if provided
            if (command.active() != null) {
                if (command.active()) {
                    product.activate();
                } else {
                    product.deactivate();
                }
            }

            // Save updated product
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to update product: " + e.getMessage());
        }
    }
}