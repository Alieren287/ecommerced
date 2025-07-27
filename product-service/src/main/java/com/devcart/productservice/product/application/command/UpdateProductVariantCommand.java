package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.Map;
import java.util.UUID;

/**
 * Command to update an existing product variant.
 */
public record UpdateProductVariantCommand(UUID productId, UUID variantId, String name,
                                          Map<String, String> attributes) implements Command {
}