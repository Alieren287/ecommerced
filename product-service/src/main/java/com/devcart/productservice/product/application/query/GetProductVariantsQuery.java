package com.devcart.productservice.product.application.query;

import com.devcart.ecommerced.core.application.port.in.Query;
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