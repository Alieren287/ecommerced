package com.alier.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Paginated response class for API responses that contain paged data
 *
 * @param <T> the type of data contained in the response
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> extends BaseResponse<List<T>> {

    private PaginationMetadata pagination;

    public PaginatedResponse() {
        super();
    }

    public PaginatedResponse(boolean success, String message, List<T> data, PaginationMetadata pagination) {
        super(success, message, data);
        this.pagination = pagination;
    }

    // Static factory methods for success responses
    public static <T> PaginatedResponse<T> success(List<T> data, PaginationMetadata pagination) {
        return new PaginatedResponse<>(true, "Success", data, pagination);
    }

    public static <T> PaginatedResponse<T> success(String message, List<T> data, PaginationMetadata pagination) {
        return new PaginatedResponse<>(true, message, data, pagination);
    }

    public static <T> PaginatedResponse<T> success(List<T> data, int page, int size, long totalElements, int totalPages) {
        PaginationMetadata pagination = new PaginationMetadata(page, size, totalElements, totalPages);
        return new PaginatedResponse<>(true, "Success", data, pagination);
    }

    // Static factory methods for error responses
    public static <T> PaginatedResponse<T> errorPaginated(String message) {
        return new PaginatedResponse<>(false, message, null, null);
    }

    public static <T> PaginatedResponse<T> errorPaginated(String message, List<ErrorDetail> errors) {
        PaginatedResponse<T> response = new PaginatedResponse<>(false, message, null, null);
        response.setErrors(errors);
        return response;
    }

    /**
     * Pagination metadata inner class
     */
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationMetadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        private boolean isFirst;
        private boolean isLast;

        public PaginationMetadata() {
        }

        public PaginationMetadata(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.hasNext = page < totalPages - 1;
            this.hasPrevious = page > 0;
            this.isFirst = page == 0;
            this.isLast = page == totalPages - 1;
        }

        public PaginationMetadata(int page, int size, long totalElements) {
            this(page, size, totalElements, (int) Math.ceil((double) totalElements / size));
        }
    }
} 