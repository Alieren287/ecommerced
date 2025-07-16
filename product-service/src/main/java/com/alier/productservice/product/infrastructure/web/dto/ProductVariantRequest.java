package com.alier.productservice.product.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for creating or updating product variants.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    @NotBlank(message = "Variant name is required")
    private String name;

    @NotBlank(message = "Variant SKU is required")
    private String sku;

    private Map<String, String> attributes;

    @NotNull(message = "Variant price is required")
    @Positive(message = "Variant price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    private String currency;
} 