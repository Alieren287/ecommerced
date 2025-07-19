package com.alier.productservice.product.infrastructure.web.dto;

import java.util.Set;

public record UpdateTagsRequest(Set<String> tags) {
}