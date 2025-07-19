package com.alier.productservice.product.infrastructure.web;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.command.*;
import com.alier.productservice.product.application.query.GetProductVariantQuery;
import com.alier.productservice.product.application.query.GetProductVariantQueryHandler;
import com.alier.productservice.product.application.query.GetProductVariantsQuery;
import com.alier.productservice.product.application.query.GetProductVariantsQueryHandler;
import com.alier.productservice.product.domain.ProductVariant;
import com.alier.productservice.product.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for product variant operations.
 */
@RestController
@RequestMapping("/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final CreateProductVariantCommandHandler createProductVariantCommandHandler;
    private final UpdateProductVariantCommandHandler updateProductVariantCommandHandler;
    private final DeleteProductVariantCommandHandler deleteProductVariantCommandHandler;
    private final GetProductVariantQueryHandler getProductVariantQueryHandler;
    private final GetProductVariantsQueryHandler getProductVariantsQueryHandler;
    private final UpdateProductVariantSkuCommandHandler updateProductVariantSkuCommandHandler;
    private final ChangeProductVariantPriceCommandHandler changeProductVariantPriceCommandHandler;
    private final ActivateProductVariantCommandHandler activateProductVariantCommandHandler;
    private final DeactivateProductVariantCommandHandler deactivateProductVariantCommandHandler;

    /**
     * Creates a new product variant.
     */
    @PostMapping
    public ResponseEntity<?> createProductVariant(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductVariantRequest request) {
        try {
            CreateProductVariantCommand command = new CreateProductVariantCommand(
                    productId,
                    request.getName(),
                    request.getSku(),
                    request.getAttributes(),
                    request.getPrice(),
                    request.getCurrency()
            );

            Result<Void> result = createProductVariantCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
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
     * Gets all variants for a product.
     */
    @GetMapping
    public ResponseEntity<?> getProductVariants(@PathVariable UUID productId) {
        try {
            GetProductVariantsQuery query = new GetProductVariantsQuery(productId);
            Result<List<ProductVariant>> result = getProductVariantsQueryHandler.handle(query);

            if (result.isSuccess()) {
                List<ProductVariant> variants = result.getValue();
                List<ProductVariantResponse> responses = variants.stream()
                        .map(this::toProductVariantResponse)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(responses);
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
     * Gets a specific variant by ID.
     */
    @GetMapping("/{variantId}")
    public ResponseEntity<?> getProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        try {
            GetProductVariantQuery query = new GetProductVariantQuery(
                    productId,
                    variantId
            );
            Result<ProductVariant> result = getProductVariantQueryHandler.handle(query);

            if (result.isSuccess()) {
                ProductVariant variant = result.getValue();
                return ResponseEntity.ok(toProductVariantResponse(variant));
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
     * Updates a product variant.
     */
    @PutMapping("/{variantId}")
    public ResponseEntity<?> updateProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        try {
            UpdateProductVariantCommand command = new UpdateProductVariantCommand(
                    productId,
                    variantId,
                    request.getName(),
                    request.getAttributes()
            );

            Result<Void> result = updateProductVariantCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping("/{variantId}/sku")
    public ResponseEntity<?> updateProductVariantSku(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateProductVariantSkuRequest request) {
        try {
            UpdateProductVariantSkuCommand command = new UpdateProductVariantSkuCommand(
                    productId,
                    variantId,
                    request.sku()
            );

            Result<Void> result = updateProductVariantSkuCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping("/{variantId}/price")
    public ResponseEntity<?> changeProductVariantPrice(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody ChangeProductVariantPriceRequest request) {
        try {
            ChangeProductVariantPriceCommand command = new ChangeProductVariantPriceCommand(
                    productId,
                    variantId,
                    request.price(),
                    request.currency()
            );

            Result<Void> result = changeProductVariantPriceCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
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
     * Deletes a product variant.
     */
    @DeleteMapping("/{variantId}")
    public ResponseEntity<?> deleteProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        try {
            DeleteProductVariantCommand command = new DeleteProductVariantCommand(
                    productId,
                    variantId
            );

            Result<Void> result = deleteProductVariantCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{variantId}/activate")
    public ResponseEntity<?> activateProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        try {
            ActivateProductVariantCommand command = new ActivateProductVariantCommand(
                    productId,
                    variantId
            );

            Result<Void> result = activateProductVariantCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{variantId}/deactivate")
    public ResponseEntity<?> deactivateProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        try {
            DeactivateProductVariantCommand command = new DeactivateProductVariantCommand(
                    productId,
                    variantId
            );

            Result<Void> result = deactivateProductVariantCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
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
     * Converts ProductVariant domain object to ProductVariantResponse DTO.
     */
    private ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getProductId().getValue(),
                variant.getName().getValue(),
                variant.getSku().getValue(),
                variant.getAttributes().getAttributes(),
                variant.getPrice().getAmount(),
                variant.getPrice().getCurrency().getCurrencyCode(),
                variant.isActive(),
                variant.getCreatedAt(),
                variant.getUpdatedAt()
        );
    }
} 