package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.ecommerced.core.domain.shared.Money;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Service
public class ChangeProductVariantPriceCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<ChangeProductVariantPriceCommand, Void> {

    public ChangeProductVariantPriceCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(ChangeProductVariantPriceCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
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
