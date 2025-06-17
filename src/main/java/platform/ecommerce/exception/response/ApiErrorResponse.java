package platform.ecommerce.exception.response;

import platform.ecommerce.exception.error.ErrorCode;

import java.time.LocalDateTime;

public record ApiErrorResponse(
    int status,
    String errorCode,
    String message,
    String path,
    LocalDateTime timestamp
) {
    public static ApiErrorResponse of(ErrorCode errorCode, int status, String path) {
        return new ApiErrorResponse(
                status,
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                LocalDateTime.now()
        );
    }

    public static ApiErrorResponse of(ErrorCode errorCode, int status, String path, String customMessage) {
        return new ApiErrorResponse(
                status,
                errorCode.getCode(),
                customMessage,
                path,
                LocalDateTime.now()
        );
    }
}
