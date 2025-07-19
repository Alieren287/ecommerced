package com.alier.productservice.product.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductVariantSkuRequest(@NotBlank String sku) {
}
