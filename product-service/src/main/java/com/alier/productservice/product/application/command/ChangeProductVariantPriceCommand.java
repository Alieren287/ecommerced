package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.math.BigDecimal;
import java.util.UUID;

public record ChangeProductVariantPriceCommand(UUID productId, UUID variantId, BigDecimal price,
                                               String currency) implements Command {
}
