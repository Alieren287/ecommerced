package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to remove an image from a product.
 */

public record RemoveProductImageCommand(UUID productId, String imageUrl) implements Command {
}