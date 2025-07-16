package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

/**
 * Command to delete a product.
 */

public record DeleteProductCommand(String productId) implements Command {
}