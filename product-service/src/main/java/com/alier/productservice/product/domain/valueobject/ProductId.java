package com.alier.productservice.product.domain.valueobject;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * Value object representing a unique product identifier.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ProductId extends ValueObject {

    private final UUID value;

    public ProductId(UUID value) {
        this.value = value;
        validate();
    }

    public ProductId(String value) {
        this.value = UUID.fromString(value);
        validate();
    }

    /**
     * Generates a new unique ProductId.
     */
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    /**
     * Creates a ProductId from a string value.
     */
    public static ProductId of(String value) {
        return new ProductId(value);
    }

    /**
     * Creates a ProductId from a UUID value.
     */
    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    @Override
    protected void validate() {
        require(value != null, "Product ID cannot be null");
    }
} 