package com.goormthon.univ.simhae.global.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // 400 Bad Request
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST.value(), "필수 입력값이 누락되었습니다"),
    INPUT_VALUE_TOO_SHORT(HttpStatus.BAD_REQUEST.value(), "입력값이 최소 길이보다 짧습니다"),
    INPUT_VALUE_TOO_LONG(HttpStatus.BAD_REQUEST.value(), "입력값이 최대 길이를 초과했습니다"),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 데이터를 찾을 수 없습니다"),
    UNCONSCIOUS_MIN_DREAMS(HttpStatus.BAD_REQUEST.value(), "무의식 분석을 위해 최소 7개의 꿈 해석이 필요합니다"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다"),
    ;

    private final int status;    // HTTP 상태 코드
    private final String message; // 에러 메시지
}
