package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the updating of existing products.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductCommandHandler implements CommandHandler<UpdateProductCommand, UUID> {

    private final ProductRepository productRepository;

    @Override
    public Result<UUID> handle(UpdateProductCommand command) {
        try {
            // Find existing product
            ProductId productId = ProductId.of(command.productId());
            var productResult = productRepository.findByProductId(productId);

            if (productResult.isFailure()) {
                return Result.failure("Failed to find product: " + productResult.getError());
            }

            var productOptional = productResult.getValue();
            if (productOptional.isEmpty()) {
                return Result.failure("Product not found with ID: " + command.productId());
            }

            Product product = productOptional.get();

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
                        .map(dto -> ProductImage.of(dto.url(), dto.altText(), dto.isPrimary(), dto.displayOrder()))
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
            return productRepository.save(product)
                    .map(savedProduct -> savedProduct.getId());

        } catch (Exception e) {
            return Result.failure("Failed to update product: " + e.getMessage());
        }
    }
} 