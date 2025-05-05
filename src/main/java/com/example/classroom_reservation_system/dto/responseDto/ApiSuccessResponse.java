package com.example.classroom_reservation_system.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiSuccessResponse<T> {

    private final boolean success = true;
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiSuccessResponse<T> of(int status, String message, T data) {
        return ApiSuccessResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiSuccessResponse<T> of(int status, String message) {
        return ApiSuccessResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
