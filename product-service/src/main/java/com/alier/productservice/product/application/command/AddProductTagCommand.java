package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to add a tag to a product.
 */

public record AddProductTagCommand(UUID productId, String tag) implements Command {
}