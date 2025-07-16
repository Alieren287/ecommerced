package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.QueryHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.domain.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Handles product retrieval queries.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProductQueryHandler implements QueryHandler<GetProductQuery, Optional<Product>> {

    private final ProductRepository productRepository;

    @Override
    public Result<Optional<Product>> handle(GetProductQuery query) {
        try {
            ProductId productId = ProductId.of(query.productId());
            return productRepository.findByProductId(productId);
        } catch (Exception e) {
            return Result.failure("Failed to retrieve product: " + e.getMessage());
        }
    }
} 