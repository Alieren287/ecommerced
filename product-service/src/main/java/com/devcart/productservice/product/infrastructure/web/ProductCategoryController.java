package com.devcart.productservice.product.infrastructure.web;

import com.devcart.ecommerced.core.application.common.Result;
import com.devcart.productservice.product.application.command.*;
import com.devcart.productservice.product.infrastructure.web.dto.ErrorResponse;
import com.devcart.productservice.product.infrastructure.web.dto.UpdateCategoriesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final AddCategoryCommandHandler addCategoryCommandHandler;
    private final RemoveCategoryCommandHandler removeCategoryCommandHandler;
    private final UpdateCategoriesCommandHandler updateCategoriesCommandHandler;

    @PostMapping
    public ResponseEntity<?> addCategory(@PathVariable UUID productId, @RequestBody Map<String, UUID> request) {
        try {
            AddCategoryCommand command = new AddCategoryCommand(productId, request.get("categoryId"));
            Result<Void> result = addCategoryCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> removeCategory(@PathVariable UUID productId, @PathVariable UUID categoryId) {
        try {
            RemoveCategoryCommand command = new RemoveCategoryCommand(productId, categoryId);
            Result<Void> result = removeCategoryCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateCategories(@PathVariable UUID productId, @RequestBody UpdateCategoriesRequest request) {
        try {
            UpdateCategoriesCommand command = new UpdateCategoriesCommand(productId, request.getCategoryIds());
            Result<Void> result = updateCategoriesCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
}
