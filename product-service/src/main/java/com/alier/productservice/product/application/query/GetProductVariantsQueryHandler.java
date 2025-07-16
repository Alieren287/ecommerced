package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.QueryHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.ProductVariant;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Query handler for getting all product variants.
 */
@Service
@RequiredArgsConstructor
public class GetProductVariantsQueryHandler implements QueryHandler<GetProductVariantsQuery, List<ProductVariant>> {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Result<List<ProductVariant>> handle(GetProductVariantsQuery query) {
        try {
            // Find the product
            Result<Optional<Product>> productResult = productRepository.findByProductId(ProductId.of(query.getProductId()));
            if (productResult.isFailure()) {
                return Result.failure(productResult.getError());
            }

            Optional<Product> productOptional = productResult.getValue();
            if (productOptional.isEmpty()) {
                return Result.failure("Product not found with ID: " + query.getProductId());
            }

            Product product = productOptional.get();

            // Get all variants
            List<ProductVariant> variants = product.getVariants();

            return Result.success(variants);

        } catch (Exception e) {
            return Result.failure("Failed to get product variants: " + e.getMessage());
        }
    }
} 