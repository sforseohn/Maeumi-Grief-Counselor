package com.gcl.maeumi.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO 유효성 검사 실패
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    protected ResponseEntity<ErrorResponseDto> handleValidationException(Exception e, HttpServletRequest request) {
        log.error("유효성 검사 실패: {}", e.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorResponseDto.of(ErrorCode.MISSING_REQUIRED_FIELD, request.getRequestURI()));
    }

    // JSON 데이터 변환 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponseDto> handleInvalidInputException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.error("입력값 또는 JSON 형식 오류: {}", e.getMessage());

        ErrorCode errorCode = determineInputErrorCode(e);

        return ResponseEntity.badRequest()
                .body(ErrorResponseDto.of(errorCode, request.getRequestURI()));
    }

    // 입력 오류
    private ErrorCode determineInputErrorCode(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();

        if (cause instanceof NumberFormatException) {
            log.error("NumberFormatException 발생: {}", cause.getMessage());
            return ErrorCode.INVALID_INPUT_FORMAT;
        }
        if (e.getMessage().contains("JSON parse error")) {
            return ErrorCode.INVALID_JSON_FORMAT;
        }
        return ErrorCode.INVALID_INPUT_FORMAT;
    }


    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomException(BusinessException e, HttpServletRequest request) {
        log.error("BusinessException 발생: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponseDto.of(e.getErrorCode(), request.getRequestURI()));
    }

    // 그 외 모든 unexpected 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> handleException(Exception e, HttpServletRequest request) {
        if (e.getMessage() != null && e.getMessage().contains("No static resource")) { // 정적 파일 예외는 로그 없이 404 응답 반환
            return ResponseEntity.notFound().build();
        }

        log.error("Unexpected Exception 발생: {}", e.getMessage());
        final ErrorResponseDto response = ErrorResponseDto.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }

}
