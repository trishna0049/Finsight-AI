package com.finsight.platform.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorPayload error,
        String timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, Instant.now().toString());
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorPayload(code, message), Instant.now().toString());
    }

    public record ErrorPayload(String code, String message) {
    }
}
