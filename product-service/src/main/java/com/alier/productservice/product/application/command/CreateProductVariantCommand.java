package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Command to create a new product variant.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantCommand implements Command {

    private UUID productId;
    private String name;
    private String sku;
    private Map<String, String> attributes;
    private BigDecimal price;
    private String currency;
} 