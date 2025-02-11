package com.gcl.maeumi.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 사용자를 찾을 수 없습니다."),

    // Question
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Q001", "해당 질문을 찾을 수 없습니다."),

    // Answer
    INVALID_USER_ANSWER(HttpStatus.BAD_REQUEST, "A001", "사용자의 답변 형식이 올바르지 않습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "A002", "해당 답변을 찾을 수 없습니다."),

    // Common
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "C001", "입력값 형식이 올바르지 않습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "C002", "JSON 형식이 잘못되었습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "C003", "필수 입력값이 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다."),

    // OpenAI
    OPENAI_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "O001", "OpenAI 요청이 실패했습니다."),
    OPENAI_RESPONSE_INVALID(HttpStatus.BAD_REQUEST, "O002", "OpenAI 응답이 올바르지 않습니다."),
    OPENAI_INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "O003", "유효하지 않은 OpenAI API 키입니다."),
    OPENAI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "O004", "OpenAI 사용량 제한을 초과했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
