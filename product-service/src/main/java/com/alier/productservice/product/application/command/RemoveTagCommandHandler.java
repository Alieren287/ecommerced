package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveTagCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<RemoveTagCommand, Void> {

    public RemoveTagCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(RemoveTagCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.removeTag(ProductTag.of(command.tag()));

            return saveProductAsVoid(product);
        } catch (Exception e) {
            return Result.failure("Failed to remove tag: " + e.getMessage());
        }
    }
}
