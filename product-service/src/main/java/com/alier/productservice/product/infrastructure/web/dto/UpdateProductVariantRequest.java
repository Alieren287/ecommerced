package com.alier.productservice.product.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating product variants.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductVariantRequest {

    @NotBlank(message = "Variant name is required")
    private String name;

    private Map<String, String> attributes;
} 