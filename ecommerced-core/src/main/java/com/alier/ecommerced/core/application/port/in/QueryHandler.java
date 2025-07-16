package com.alier.ecommerced.core.application.port.in;

import com.alier.ecommerced.core.application.common.Result;

/**
 * Interface for query handlers in the CQRS pattern.
 * Query handlers process queries and return results.
 *
 * @param <Q> the query type
 * @param <R> the result type
 */
public interface QueryHandler<Q extends Query, R> extends UseCase {

    /**
     * Handles the given query and returns a result.
     *
     * @param query the query to handle
     * @return the result of handling the query
     */
    Result<R> handle(Q query);
} 