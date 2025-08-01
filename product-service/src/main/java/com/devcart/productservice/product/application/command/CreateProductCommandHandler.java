package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.*;
import com.devcart.productservice.product.infrastructure.web.dto.ProductImageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Command handler for creating products.
 */
@Service
public class CreateProductCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<CreateProductCommand, UUID> {

    public CreateProductCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<UUID> handle(CreateProductCommand command) {
        try {
            // Create value objects
            ProductName name = ProductName.of(command.name());
            ProductDescription description = ProductDescription.of(command.description());
            ProductSlug slug = ProductSlug.of(command.slug());
            ProductAttributes attributes = ProductAttributes.of(command.attributes());

            // Create product
            Product product = new Product(name, description, slug, attributes, command.categoryIds());

            // Add tags if provided
            if (command.tags() != null) {
                for (String tagValue : command.tags()) {
                    product.addTag(ProductTag.of(tagValue));
                }
            }

            // Add images if provided
            // Map image DTOs to domain objects
            if (command.images() != null && !command.images().isEmpty()) {
                command.images().stream()
                        .map(ProductImageDto::mapToProductImage)
                        .forEach(product::addImage);
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