package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.port.in.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a specific product variant.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductVariantQuery implements Query {

    private UUID productId;
    private UUID variantId;
} 