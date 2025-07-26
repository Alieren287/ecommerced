package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import com.alier.productservice.product.domain.valueobject.VariantSku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductVariantSkuCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<UpdateProductVariantSkuCommand, Void> {


    public UpdateProductVariantSkuCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(UpdateProductVariantSkuCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.updateVariantSku(ProductVariantId.of(command.variantId()), VariantSku.of(command.sku()));

            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();
        } catch (Exception e) {
            return Result.failure("Failed to update product variant SKU: " + e.getMessage());
        }
    }
}
