package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import com.alier.productservice.product.infrastructure.web.dto.ProductImageDto;

import java.util.UUID;

/**
 * Command to add an image to a product.
 */
public record AddProductImageCommand(UUID productId, ProductImageDto image) implements Command {
}