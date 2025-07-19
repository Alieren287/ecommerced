package com.alier.productservice.product.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChangeProductVariantPriceRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotBlank String currency) {
}
