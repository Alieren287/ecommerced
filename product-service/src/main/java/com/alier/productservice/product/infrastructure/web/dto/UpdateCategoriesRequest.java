package com.alier.productservice.product.infrastructure.web.dto;

import java.util.Set;
import java.util.UUID;

public record UpdateCategoriesRequest(Set<UUID> categoryIds) {
}
