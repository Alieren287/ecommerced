package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.ProductVariantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateProductVariantCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<DeactivateProductVariantCommand, Void> {

    public DeactivateProductVariantCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(DeactivateProductVariantCommand command) {
        try {
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.deactivateVariant(ProductVariantId.of(command.variantId()));

            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to deactivate product variant: " + e.getMessage());
        }
    }
}
