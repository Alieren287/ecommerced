package com.devcart.productservice.product.domain.valueobject;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Value object representing product variant attributes (e.g., color, size, material).
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class VariantAttributes extends ValueObject {

    private final Map<String, String> attributes;

    public VariantAttributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes != null ? attributes : Collections.emptyMap());
        validate();
    }

    /**
     * Creates VariantAttributes from a map.
     */
    public static VariantAttributes of(Map<String, String> attributes) {
        return new VariantAttributes(attributes);
    }

    /**
     * Creates empty VariantAttributes.
     */
    public static VariantAttributes empty() {
        return new VariantAttributes(Collections.emptyMap());
    }

    /**
     * Gets an attribute value by key.
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Checks if an attribute exists.
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Gets all attribute keys.
     */
    public Set<String> getAttributeKeys() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /**
     * Gets all attributes as an unmodifiable map.
     */
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    protected void validate() {
        require(attributes != null, "Variant attributes cannot be null");

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            require(key != null && !key.trim().isEmpty(), "Attribute key cannot be null or empty");
            require(key.length() <= 50, "Attribute key cannot exceed 50 characters");
            require(value != null && !value.trim().isEmpty(), "Attribute value cannot be null or empty");
            require(value.length() <= 255, "Attribute value cannot exceed 255 characters");
        }

        require(attributes.size() <= 20, "Cannot have more than 20 attributes per variant");
    }
} 