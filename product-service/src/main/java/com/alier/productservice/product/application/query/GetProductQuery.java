package com.alier.productservice.product.application.query;

import com.alier.ecommerced.core.application.port.in.Query;

/**
 * Query to get a product by ID.
 */

public record GetProductQuery(String productId) implements Query {
}