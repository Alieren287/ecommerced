package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import com.alier.productservice.product.infrastructure.web.dto.ProductImageDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to create a new product.
 */
public record CreateProductCommand(String name, String description, String slug, Map<String, String> attributes,
                                   Set<UUID> categoryIds, Set<String> tags,
                                   List<ProductImageDto> images) implements Command {
}