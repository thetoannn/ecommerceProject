package com.example.ecommercephone.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String redirect;

    public static <T> RestApiResponse<T> success(T data) {
        return RestApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> RestApiResponse<T> success(String message, T data) {
        return RestApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> RestApiResponse<T> error(String message) {
        return RestApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> RestApiResponse<T> error(String message, String redirect) {
        return RestApiResponse.<T>builder()
                .success(false)
                .message(message)
                .redirect(redirect)
                .build();
    }
}
