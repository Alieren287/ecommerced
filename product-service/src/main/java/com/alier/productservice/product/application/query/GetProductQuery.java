package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.port.in.Query;

import java.util.UUID;

/**
 * Query to get a product by ID.
 */

public record GetProductQuery(UUID productId) implements Query {
}