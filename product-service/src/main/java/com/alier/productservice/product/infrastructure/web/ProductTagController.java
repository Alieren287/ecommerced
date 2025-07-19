package com.alier.productservice.product.infrastructure.web;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.command.*;
import com.alier.productservice.product.infrastructure.web.dto.ErrorResponse;
import com.alier.productservice.product.infrastructure.web.dto.UpdateTagsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/tags")
@RequiredArgsConstructor
public class ProductTagController {

    private final AddTagCommandHandler addTagCommandHandler;
    private final RemoveTagCommandHandler removeTagCommandHandler;
    private final UpdateTagsCommandHandler updateTagsCommandHandler;

    @PostMapping
    public ResponseEntity<?> addTag(@PathVariable UUID productId, @RequestBody Map<String, String> request) {
        try {
            AddTagCommand command = new AddTagCommand(productId, request.get("tag"));
            Result<Void> result = addTagCommandHandler.handle(command);

            if (result.isSuccess()) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeTag(@PathVariable UUID productId, @RequestBody Map<String, String> request) {
        try {
            RemoveTagCommand command = new RemoveTagCommand(productId, request.get("tag"));
            Result<Void> result = removeTagCommandHandler.handle(command);

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
    public ResponseEntity<?> updateTags(@PathVariable UUID productId, @RequestBody UpdateTagsRequest request) {
        try {
            UpdateTagsCommand command = new UpdateTagsCommand(productId, request.tags());
            Result<Void> result = updateTagsCommandHandler.handle(command);

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
