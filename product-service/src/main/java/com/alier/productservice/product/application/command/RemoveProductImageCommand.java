package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

/**
 * Command to remove an image from a product.
 */

public record RemoveProductImageCommand(String productId, String imageUrl) implements Command {
}