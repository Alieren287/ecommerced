package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.UUID;

public record AddTagCommand(UUID productId, String tag) implements Command {
}
