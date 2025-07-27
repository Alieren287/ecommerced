package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Command to create a new product variant.
 */
public record CreateProductVariantCommand(UUID productId, String name, String sku, Map<String, String> attributes,
                                          BigDecimal price, String currency) implements Command {
}