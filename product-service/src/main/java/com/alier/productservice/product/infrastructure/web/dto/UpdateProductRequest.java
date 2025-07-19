package com.alier.productservice.product.infrastructure.web.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record UpdateProductRequest(UUID productId, String name, String description, String slug,
                                   Map<String, String> attributes, Set<UUID> categoryIds, Set<String> tags,
                                   List<ProductImageDto> images, Boolean active) {
}
