package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

/**
 * Command to remove a tag from a product.
 */

public record RemoveProductTagCommand(String productId, String tag) implements Command {
}