package com.alier.ecommerced.core.application.port.in;

import com.alier.ecommerced.core.application.common.Result;

/**
 * Interface for command handlers in the CQRS pattern.
 * Command handlers process commands and return results.
 *
 * @param <C> the command type
 * @param <R> the result type
 */
public interface CommandHandler<C extends Command, R> extends UseCase {

    /**
     * Handles the given command and returns a result.
     *
     * @param command the command to handle
     * @return the result of handling the command
     */
    Result<R> handle(C command);
} 