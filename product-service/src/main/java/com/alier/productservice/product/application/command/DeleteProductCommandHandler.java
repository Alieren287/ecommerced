package com.alier.productservice.product.application.command;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.application.port.in.CommandHandler;
import com.alier.productservice.product.application.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProductCommandHandler implements CommandHandler<DeleteProductCommand, Void> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Result<Void> handle(DeleteProductCommand command) {
        try {
            productRepository.deleteById(command.productId());
            return Result.success();
        } catch (Exception e) {
            return Result.failure("Failed to delete product: " + e.getMessage());
        }
    }
}