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

    // Common
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "C001", "입력값 형식이 올바르지 않습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "C002", "JSON 형식이 잘못되었습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "C003", "필수 입력값이 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
