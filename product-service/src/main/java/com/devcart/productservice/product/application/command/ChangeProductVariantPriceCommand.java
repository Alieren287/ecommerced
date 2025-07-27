package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;

import java.math.BigDecimal;
import java.util.UUID;

public record ChangeProductVariantPriceCommand(UUID productId, UUID variantId, BigDecimal price,
                                               String currency) implements Command {
}
