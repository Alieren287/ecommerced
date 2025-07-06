package com.alier.core.controller;

import com.alier.core.response.BaseResponse;

/**
 * Base controller class providing common utility methods for creating responses
 */
public abstract class BaseController {

    /**
     * Creates a successful response with data
     *
     * @param data the data to include in the response
     * @param <T>  the data type
     * @return BaseResponse containing the data
     */
    protected <T> BaseResponse<T> success(T data) {
        return BaseResponse.success(data);
    }

    /**
     * Creates a successful response with custom message and data
     *
     * @param message custom success message
     * @param data    the data to include in the response
     * @param <T>     the data type
     * @return BaseResponse containing the data
     */
    protected <T> BaseResponse<T> success(String message, T data) {
        return BaseResponse.success(message, data);
    }

    /**
     * Creates a successful response without data
     *
     * @return BaseResponse
     */
    protected BaseResponse<Void> success() {
        return BaseResponse.success();
    }

    /**
     * Creates a successful response with custom message and no data
     *
     * @param message custom success message
     * @return BaseResponse
     */
    protected BaseResponse<Void> success(String message) {
        return BaseResponse.success(message, null);
    }

    /**
     * Creates an error response with message
     *
     * @param message error message
     * @param <T>     the data type
     * @return BaseResponse with error
     */
    protected <T> BaseResponse<T> error(String message) {
        return BaseResponse.error(message);
    }

    /**
     * Creates an error response with message and error details
     *
     * @param message error message
     * @param errors  error details
     * @param <T>     the data type
     * @return BaseResponse with error
     */
    protected <T> BaseResponse<T> error(String message, java.util.List<com.alier.core.response.ErrorDetail> errors) {
        return BaseResponse.error(message, errors);
    }
} 