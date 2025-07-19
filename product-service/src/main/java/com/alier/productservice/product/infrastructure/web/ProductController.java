package com.alier.productservice.product.infrastructure.web;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.command.*;
import com.alier.productservice.product.application.query.GetProductQuery;
import com.alier.productservice.product.application.query.GetProductQueryHandler;
import com.alier.productservice.product.domain.Product;
import com.alier.productservice.product.infrastructure.web.dto.*;
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
    private final UpdateProductCommandHandler updateProductCommandHandler;
    private final DeleteProductCommandHandler deleteProductCommandHandler;
    private final ActivateProductCommandHandler activateProductCommandHandler;
    private final DeactivateProductCommandHandler deactivateProductCommandHandler;

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
     * Updates a product's basic information.
     */
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId, @RequestBody UpdateProductRequest request) {
        try {
            UpdateProductCommand command = new UpdateProductCommand(
                    productId,
                    request.name(),
                    request.description(),
                    request.slug(),
                    request.attributes(),
                    request.categoryIds(),
                    request.tags(),
                    request.images(),
                    request.active()
            );

            Result<Void> result = updateProductCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Deletes a product by ID.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId) {
        try {
            DeleteProductCommand command = new DeleteProductCommand(productId);
            Result<Void> result = deleteProductCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{productId}/activate")
    public ResponseEntity<?> activateProduct(@PathVariable UUID productId) {
        try {
            ActivateProductCommand command = new ActivateProductCommand(productId);
            Result<Void> result = activateProductCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{productId}/deactivate")
    public ResponseEntity<?> deactivateProduct(@PathVariable UUID productId) {
        try {
            DeactivateProductCommand command = new DeactivateProductCommand(productId);
            Result<Void> result = deactivateProductCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
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