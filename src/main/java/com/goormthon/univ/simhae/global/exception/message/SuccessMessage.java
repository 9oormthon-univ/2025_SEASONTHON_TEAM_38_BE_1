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
    LOAD_SUCCESS(HttpStatus.OK.value(),"꿈 조회가 완료되었습니다"),

    /*
    201 Created
     */
    CREATE_SUCCESS(HttpStatus.CREATED.value(), "꿈 해몽 생성이 완료되었습니다"),
    UNCONSCIOUS_SUCCESS(HttpStatus.CREATED.value(), "무의식 분석이 완료되었습니다"),

    /*
    204 No Content
     */
    DELETED_SUCCESS(HttpStatus.NO_CONTENT.value(), "삭제가 완료되었습니다");
    ;

    private final int status;    // HTTP 상태 코드
    private final String message; // 성공 메시지
}
