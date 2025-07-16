package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.port.in.Command;
import com.alier.productservice.product.domain.valueobject.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command to create a new product.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCommand implements Command {

    private String name;
    private String description;
    private String slug;
    private Map<String, String> attributes;
    private Set<UUID> categoryIds;
    private Set<String> tags;
    private List<ProductImage> images;
}