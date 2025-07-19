package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to delete a product variant.
 */
public record DeleteProductVariantCommand(UUID productId, UUID variantId) implements Command {
}