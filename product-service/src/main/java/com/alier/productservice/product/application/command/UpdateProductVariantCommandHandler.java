package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductVariantId;
import com.alier.productservice.product.domain.valueobject.VariantAttributes;
import com.alier.productservice.product.domain.valueobject.VariantName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command handler for updating product variants.
 */
@Service
public class UpdateProductVariantCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<UpdateProductVariantCommand, Void> {

    public UpdateProductVariantCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(UpdateProductVariantCommand command) {
        try {
            // Find the product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();

            // Create value objects
            ProductVariantId variantId = ProductVariantId.of(command.variantId());
            VariantName name = VariantName.of(command.name());
            VariantAttributes attributes = VariantAttributes.of(command.attributes());

            // Update variant
            product.updateVariant(variantId, name, attributes);

            // Save product
            return saveProductAsVoid(product);

        } catch (Exception e) {
            return Result.failure("Failed to update product variant: " + e.getMessage());
        }
    }
} 