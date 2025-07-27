package com.devcart.productservice.product.application.query;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.ecommerced.core.application.port.in.QueryHandler;
import com.devcart.productservice.product.application.port.out.ProductRepository;
import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.ProductVariant;
import com.devcart.productservice.product.domain.valueobject.ProductId;
import com.devcart.productservice.product.domain.valueobject.ProductVariantId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Query handler for getting product variants.
 */
@Service
@RequiredArgsConstructor
public class GetProductVariantQueryHandler implements QueryHandler<GetProductVariantQuery, ProductVariant> {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Result<ProductVariant> handle(GetProductVariantQuery query) {
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

            // Find the variant
            ProductVariant variant = product.findVariantById(ProductVariantId.of(query.getVariantId()));
            if (variant == null) {
                return Result.failure("Variant not found with ID: " + query.getVariantId());
            }

            return Result.success(variant);

        } catch (Exception e) {
            return Result.failure("Failed to get product variant: " + e.getMessage());
        }
    }
} 