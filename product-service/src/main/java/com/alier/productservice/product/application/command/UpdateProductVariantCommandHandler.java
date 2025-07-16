package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import com.alier.productservice.product.domain.valueobject.VariantAttributes;
import com.alier.productservice.product.domain.valueobject.VariantName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Command handler for updating product variants.
 */
@Service
@RequiredArgsConstructor
public class UpdateProductVariantCommandHandler implements CommandHandler<UpdateProductVariantCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<Void> handle(UpdateProductVariantCommand command) {
        try {
            // Find the product
            Result<Optional<Product>> productResult = productRepository.findByProductId(ProductId.of(command.getProductId()));
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Optional<Product> productOptional = productResult.getValue();
            if (productOptional.isEmpty()) {
                return Result.failure("Product not found with ID: " + command.getProductId());
            }

            Product product = productOptional.get();

            // Create value objects
            ProductVariantId variantId = ProductVariantId.of(command.getVariantId());
            VariantName name = VariantName.of(command.getName());
            VariantAttributes attributes = VariantAttributes.of(command.getAttributes());

            // Update variant
            product.updateVariant(variantId, name, attributes);

            // Save product
            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();

        } catch (Exception e) {
            return Result.failure("Failed to update product variant: " + e.getMessage());
        }
    }
} 