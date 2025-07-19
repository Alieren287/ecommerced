package com.alier.productservice.product.infrastructure.web;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.productservice.product.application.command.AddProductImageCommand;
import com.alier.productservice.product.application.command.AddProductImageCommandHandler;
import com.alier.productservice.product.application.command.RemoveProductImageCommand;
import com.alier.productservice.product.application.command.RemoveProductImageCommandHandler;
import com.alier.productservice.product.infrastructure.web.dto.ErrorResponse;
import com.alier.productservice.product.infrastructure.web.dto.ProductImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final AddProductImageCommandHandler addProductImageCommandHandler;
    private final RemoveProductImageCommandHandler removeProductImageCommandHandler;

    @PostMapping
    public ResponseEntity<?> addProductImage(@PathVariable UUID productId, @RequestBody ProductImageDto imageDto) {
        try {
            AddProductImageCommand command = new AddProductImageCommand(productId, imageDto);
            Result<Void> result = addProductImageCommandHandler.handle(command);

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
    public ResponseEntity<?> removeProductImage(@PathVariable UUID productId, @RequestBody ProductImageDto imageDto) {
        try {
            RemoveProductImageCommand command = new RemoveProductImageCommand(productId, imageDto);
            Result<Void> result = removeProductImageCommandHandler.handle(command);

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
