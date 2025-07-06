package com.alier.core.usecase;

/**
 * Base interface for all use cases in the hexagonal architecture
 *
 * @param <INPUT>  the input type for the use case
 * @param <OUTPUT> the output type for the use case
 */
public interface UseCase<INPUT, OUTPUT> {

    /**
     * Executes the use case with the provided input
     *
     * @param input the input data for the use case
     * @return the output result of the use case execution
     */
    OUTPUT execute(INPUT input);
} 