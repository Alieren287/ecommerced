package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateTagsCommandHandler implements CommandHandler<UpdateTagsCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<Void> handle(UpdateTagsCommand command) {
        try {
            Result<Optional<Product>> productResult = productRepository.findById(command.productId());
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            if (productResult.getValue().isEmpty()) {
                return Result.failure("Product not found");
            }

            Product product = productResult.getValue().get();
            Set<ProductTag> productTags = command.tags().stream()
                    .map(ProductTag::of)
                    .collect(Collectors.toSet());
            product.updateTags(productTags);

            Result<Product> saveResult = productRepository.save(product);
            if (saveResult.isFailure()) {
                return Result.failure(saveResult.getError());
            }

            return Result.success();
        } catch (Exception e) {
            return Result.failure("Failed to update tags: " + e.getMessage());
        }
    }
}
