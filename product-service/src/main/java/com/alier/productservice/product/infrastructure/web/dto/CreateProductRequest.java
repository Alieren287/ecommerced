package com.alier.productservice.product.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for creating a new product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    private String name;
    private String description;
    private String slug;
    private Map<String, String> attributes;
    private Set<UUID> categoryIds;
    private Set<String> tags;
    private List<ProductImageDto> images;
}
