package com.devcart.productservice.product.application.command;

import com.devcart.ecommerced.core.application.port.in.Command;
import com.devcart.productservice.product.infrastructure.web.dto.ProductImageDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to update an existing product.
 */

public record UpdateProductCommand(UUID productId, String name, String description, String slug,
                                   Map<String, String> attributes, Set<UUID> categoryIds, Set<String> tags,
                                   List<ProductImageDto> images, Boolean active) implements Command {
}