package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

/**
 * Command to add a tag to a product.
 */

public record AddProductTagCommand(String productId, String tag) implements Command {
}