package com.goormthon.univ.simhae.global.dto;

import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;

public record SuccessResponse<T>(
        int status,
        String message,
        T data
) {
    public static <T> SuccessResponse of(final SuccessMessage successMessage, final T data) {
        return new SuccessResponse(successMessage.getStatus(), successMessage.getMessage(), data);
    }

    public static SuccessResponse of(final SuccessMessage successMessage) {
        return new SuccessResponse(successMessage.getStatus(), successMessage.getMessage(), null);
    }
}