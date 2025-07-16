package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Command handler for creating products.
 */
@Service
@RequiredArgsConstructor
public class CreateProductCommandHandler implements CommandHandler<CreateProductCommand, UUID> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<UUID> handle(CreateProductCommand command) {
        try {
            // Create value objects
            ProductName name = ProductName.of(command.getName());
            ProductDescription description = ProductDescription.of(command.getDescription());
            ProductSlug slug = ProductSlug.of(command.getSlug());
            ProductAttributes attributes = ProductAttributes.of(command.getAttributes());

            // Create product
            Product product = new Product(name, description, slug, attributes, command.getCategoryIds());

            // Add tags if provided
            if (command.getTags() != null) {
                for (String tagValue : command.getTags()) {
                    product.addTag(ProductTag.of(tagValue));
                }
            }

            // Add images if provided
            if (command.getImages() != null) {
                for (ProductImage image : command.getImages()) {
                    product.addImage(image);
                }
            }

            // Save product
            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success(product.getId());

        } catch (Exception e) {
            return Result.failure("Failed to create product: " + e.getMessage());
        }
    }
} 