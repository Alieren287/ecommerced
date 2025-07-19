package com.alier.productservice.product.infrastructure.web;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.command.CreateProductCommand;
import com.alier.productservice.product.application.command.CreateProductCommandHandler;
import com.alier.productservice.product.application.query.GetProductQuery;
import com.alier.productservice.product.application.query.GetProductQueryHandler;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.infrastructure.web.dto.CreateProductRequest;
import com.alier.productservice.product.infrastructure.web.dto.CreateProductResponse;
import com.alier.productservice.product.infrastructure.web.dto.ErrorResponse;
import com.alier.productservice.product.infrastructure.web.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for product operations.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final GetProductQueryHandler getProductQueryHandler;

    /**
     * Creates a new product.
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        try {
            CreateProductCommand command = new CreateProductCommand(
                    request.getName(),
                    request.getDescription(),
                    request.getSlug(),
                    request.getAttributes(),
                    request.getCategoryIds(),
                    request.getTags(),
                    request.getImages()
            );

            Result<UUID> result = createProductCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new CreateProductResponse(result.getValue()));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Gets a product by ID.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        try {
            GetProductQuery query = new GetProductQuery(productId);
            Result<Optional<Product>> result = getProductQueryHandler.handle(query);

            if (result.isSuccess()) {
                Optional<Product> product = result.getValue();
                if (product.isPresent()) {
                    return ResponseEntity.ok(ProductResponse.from(product.get()));
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product Service is running");
    }
} 