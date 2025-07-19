package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangeProductVariantPriceCommandHandler implements CommandHandler<ChangeProductVariantPriceCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<Void> handle(ChangeProductVariantPriceCommand command) {
        try {
            Result<Optional<Product>> productResult = productRepository.findById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            if (productResult.getValue().isEmpty()) {
                return Result.failure("Product not found");
            }

            Product product = productResult.getValue().get();
            product.changeVariantPrice(ProductVariantId.of(command.variantId()), new Money(command.price(), Currency.getInstance(command.currency())));

            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();
        } catch (Exception e) {
            return Result.failure("Failed to change product variant price: " + e.getMessage());
        }
    }
}
