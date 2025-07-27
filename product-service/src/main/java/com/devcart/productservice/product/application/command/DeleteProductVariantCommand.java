package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to delete a product variant.
 */
public record DeleteProductVariantCommand(UUID productId, UUID variantId) implements Command {
}