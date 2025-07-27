package com.devcart.productservice.product.application.query;

import com.devcart.ecommerced.core.application.port.in.Query;

import java.util.UUID;

/**
 * Query to get a product by ID.
 */

public record GetProductQuery(UUID productId) implements Query {
}