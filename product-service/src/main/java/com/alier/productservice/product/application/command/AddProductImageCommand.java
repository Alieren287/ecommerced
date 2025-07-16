package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

/**
 * Command to add an image to a product.
 */

public record AddProductImageCommand(String productId, String url, String altText, boolean isPrimary,
                                     int displayOrder) implements Command {
}