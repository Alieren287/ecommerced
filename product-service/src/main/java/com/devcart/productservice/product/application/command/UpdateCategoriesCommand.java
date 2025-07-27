package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.Set;
import java.util.UUID;

public record UpdateCategoriesCommand(UUID productId, Set<UUID> categoryIds) implements Command {
}
