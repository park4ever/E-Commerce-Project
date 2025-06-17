package platform.ecommerce.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import platform.ecommerce.exception.error.ErrorCode;
import platform.ecommerce.exception.response.ApiErrorResponse;

import static org.springframework.http.HttpStatus.*;
import static platform.ecommerce.exception.error.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 관련 예외 처리 (사용자 정의 예외)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[BusinessException] {} : {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ApiErrorResponse.of(errorCode, BAD_REQUEST.value(), request.getRequestURI()));
    }

    /**
     * UsernameNotFoundException (Spring Security 관련)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFound(Exception e, HttpServletRequest request) {
        log.warn("[UsernameNotFoundException] {}", e.getMessage());

        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ApiErrorResponse.of(MEMBER_NOT_FOUND, UNAUTHORIZED.value(), request.getRequestURI()));
    }

    /**
     * EntityNotFoundException (JPA 관련)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(Exception e, HttpServletRequest request) {
        log.warn("[EntityNotFoundException] {}", e.getMessage());

        return ResponseEntity
                .status(NOT_FOUND)
                .body(ApiErrorResponse.of(INVALID_REQUEST, NOT_FOUND.value(), request.getRequestURI()));
    }

    /**
     * 그 외 모든 예외 (예상치 못한 예외들)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception e, HttpServletRequest request) {
        log.warn("[UnhandledException] ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()));
    }
}
