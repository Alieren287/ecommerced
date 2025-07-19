package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.UUID;

public record DeactivateProductVariantCommand(UUID productId, UUID variantId) implements Command {
}
