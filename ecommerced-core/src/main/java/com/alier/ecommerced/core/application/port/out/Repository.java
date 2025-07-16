package com.alier.ecommerced.core.application.port.out;

import com.alier.ecommerced.core.application.common.Result;
import com.alier.ecommerced.core.domain.common.AggregateRoot;

import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for output ports in the hexagonal architecture.
 * Repositories provide an abstraction for accessing and persisting aggregate roots.
 *
 * @param <T> the aggregate root type
 */
public interface Repository<T extends AggregateRoot> {

    /**
     * Saves an aggregate root.
     *
     * @param aggregate the aggregate root to save
     * @return the saved aggregate root
     */
    Result<T> save(T aggregate);

    /**
     * Finds an aggregate root by its ID.
     *
     * @param id the ID of the aggregate root
     * @return the aggregate root if found, empty otherwise
     */
    Result<Optional<T>> findById(UUID id);

    /**
     * Deletes an aggregate root by its ID.
     *
     * @param id the ID of the aggregate root to delete
     * @return success if deleted, failure otherwise
     */
    Result<Void> deleteById(UUID id);

    /**
     * Checks if an aggregate root exists by its ID.
     *
     * @param id the ID of the aggregate root
     * @return true if exists, false otherwise
     */
    Result<Boolean> existsById(UUID id);
} 