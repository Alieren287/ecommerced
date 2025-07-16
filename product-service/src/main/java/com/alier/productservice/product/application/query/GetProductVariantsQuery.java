package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.port.in.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get all variants of a product.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductVariantsQuery implements Query {

    private UUID productId;
} 