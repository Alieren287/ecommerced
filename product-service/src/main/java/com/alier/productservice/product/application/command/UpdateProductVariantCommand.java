package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to update an existing product variant.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductVariantCommand implements Command {

    private UUID productId;
    private UUID variantId;
    private String name;
    private Map<String, String> attributes;
} 