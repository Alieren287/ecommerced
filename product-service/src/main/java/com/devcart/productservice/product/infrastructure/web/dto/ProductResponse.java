package com.devcart.productservice.product.infrastructure.web.dto;

import com.devcart.productservice.product.domain.Product;
import com.devcart.productservice.product.domain.valueobject.ProductTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Response DTO for product information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String slug;
    private Map<String, String> attributes;
    private Set<UUID> categoryIds;
    private Set<String> tags;
    private boolean active;
    private int totalVariants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates a ProductResponse from a Product domain object.
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName().getValue(),
                product.getDescription().getValue(),
                product.getSlug().getValue(),
                product.getAttributes().getAttributes(),
                product.getCategoryIds(),
                product.getTags().stream()
                        .map(ProductTag::getValue)
                        .collect(Collectors.toSet()),
                product.isActive(),
                product.getVariants().size(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
