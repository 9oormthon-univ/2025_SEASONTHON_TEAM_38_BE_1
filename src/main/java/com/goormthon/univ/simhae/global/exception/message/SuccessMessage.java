package com.goormthon.univ.simhae.global.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    /*
    200 OK
     */
    AUTH_SUCCESS(HttpStatus.OK.value(), "로그인 완료"),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK.value(), "토큰 재발급 완료"),
    LOAD_SUCCESS(HttpStatus.OK.value(),"꿈 조회가 완료되었습니다"),
    REGISTER_SUCCESS(HttpStatus.OK.value(),"아이디가 등록되었습니다"),


    /*
    201 Created
     */
    CREATE_SUCCESS(HttpStatus.CREATED.value(), "꿈 해몽 생성이 완료되었습니다"),
    UNCONSCIOUS_SUCCESS(HttpStatus.CREATED.value(), "무의식 분석이 완료되었습니다"),

    /*
    204 No Content
     */
    DELETED_SUCCESS(HttpStatus.NO_CONTENT.value(), "삭제가 완료되었습니다"),
    LOGOUT_SUCCESS(HttpStatus.NO_CONTENT.value(), "로그아웃 완료");
    ;

    private final int status;    // HTTP 상태 코드
    private final String message; // 성공 메시지
}
