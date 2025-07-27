package com.devcart.productservice.product.infrastructure.web.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateProductResponse {
    private final UUID productId;

    public CreateProductResponse(UUID productId) {
        this.productId = productId;
    }

}
