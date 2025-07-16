package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to update an existing product.
 */

public record UpdateProductCommand(String productId, String name, String description, String slug,
                                   Map<String, String> attributes, Set<UUID> categoryIds, Set<String> tags,
                                   List<ProductImageDto> images, Boolean active) implements Command {

    public record ProductImageDto(String url, String altText, boolean isPrimary, int displayOrder) {
    }
} 