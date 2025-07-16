package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to delete a product variant.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductVariantCommand implements Command {

    private UUID productId;
    private UUID variantId;
} 