package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.util.UUID;

public record AddCategoryCommand(UUID productId, UUID categoryId) implements Command {
}
