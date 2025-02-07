package com.gcl.maeumi.common.error;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        int status,
        String name,
        String code,
        String message,
        String uriPath,
        String timestamp
) {
    public static ErrorResponseDto of(ErrorCode errorCode, String uriPath) {
        return new ErrorResponseDto(
                errorCode.getStatus().value(),
                errorCode.name(),
                errorCode.getCode(),
                errorCode.getMessage(),
                uriPath,
                LocalDateTime.now().toString()
        );
    }
}

