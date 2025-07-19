package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to add an image to a product.
 */

public record AddProductImageCommand(UUID productId, String url, String altText, boolean isPrimary,
                                     int displayOrder) implements Command {
}