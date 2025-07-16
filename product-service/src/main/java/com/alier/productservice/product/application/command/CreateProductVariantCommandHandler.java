package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import com.alier.productservice.product.domain.valueobject.VariantAttributes;
import com.alier.productservice.product.domain.valueobject.VariantName;
import com.alier.productservice.product.domain.valueobject.VariantSku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Optional;

/**
 * Command handler for creating product variants.
 */
@Service
@RequiredArgsConstructor
public class CreateProductVariantCommandHandler implements CommandHandler<CreateProductVariantCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<Void> handle(CreateProductVariantCommand command) {
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
            VariantName name = VariantName.of(command.getName());
            VariantSku sku = VariantSku.of(command.getSku());
            VariantAttributes attributes = VariantAttributes.of(command.getAttributes());
            Money price = new Money(command.getPrice(), Currency.getInstance(command.getCurrency()));

            // Add variant to product
            product.addVariant(name, sku, attributes, price);

            // Save product
            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();

        } catch (Exception e) {
            return Result.failure("Failed to create product variant: " + e.getMessage());
        }
    }
} 