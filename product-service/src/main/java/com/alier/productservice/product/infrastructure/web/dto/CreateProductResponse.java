package com.alier.productservice.product.infrastructure.web.dto;

import lombok.Getter;

@Getter
public class CreateProductResponse {
    private final String productId;

    public CreateProductResponse(String productId) {
        this.productId = productId;
    }

}
