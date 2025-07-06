package com.alier.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Detail error information for API responses
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

    private String code;
    private String message;
    private String field;
    private Object rejectedValue;

    public ErrorDetail(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorDetail(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }
} 