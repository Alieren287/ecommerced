package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.CommandHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCategoriesCommandHandler extends BaseProductCommandHandler
        implements CommandHandler<UpdateCategoriesCommand, Void> {

    public UpdateCategoriesCommandHandler(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @Transactional
    public Result<Void> handle(UpdateCategoriesCommand command) {
        try {
            // Find product using base class method
            var productResult = getProductById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Product product = productResult.getValue();
            product.updateCategories(command.categoryIds());

            return saveProductAsVoid(product);
        } catch (Exception e) {
            return Result.failure("Failed to update categories: " + e.getMessage());
        }
    }
}
