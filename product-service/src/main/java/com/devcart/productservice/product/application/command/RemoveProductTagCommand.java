package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.UUID;

/**
 * Command to remove a tag from a product.
 */

public record RemoveProductTagCommand(UUID productId, String tag) implements Command {
}