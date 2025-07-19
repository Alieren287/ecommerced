package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.VariantAttributes;
import com.alier.productservice.product.domain.valueobject.VariantName;
import com.alier.productservice.product.domain.valueobject.VariantSku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

/**
 * Command handler for creating product variants.
 */
@Service
public class CreateProductVariantCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<CreateProductVariantCommand, Void> {

    public CreateProductVariantCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(CreateProductVariantCommand command) {
        try {
            // Find the product using base class method
            var productResult = findProductById(command.getProductId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

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