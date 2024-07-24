package com.canvi.hama.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /** 성공 2xx */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /** client error - 4xx */

    INVALID_EMAIL_FORMAT(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 이메일 형식입니다."),
    NON_EXIST_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(false, HttpStatus.UNAUTHORIZED.value(), "토큰을 찾을 수 없습니다."),
    EXPIRED_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    INVALID_CREDENTIALS(false, HttpStatus.UNAUTHORIZED.value(), "아이디 또는 비밀번호가 유효하지 않습니다."),
    INVALID_AUTH_CODE(false, HttpStatus.UNAUTHORIZED.value(), "인증번호가 올바르지 않습니다."),
    EMAIL_NOT_VERIFIED(false, HttpStatus.UNAUTHORIZED.value(), "이메일이 인증되지 않았습니다."),
    USERNAME_ALREADY_EXISTS(false, HttpStatus.CONFLICT.value(), "이미 존재하는 아이디입니다."),
    EMAIL_ALREADY_EXISTS(false, HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다."),


    /** server error - 5xx */
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."),

    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    /**
     *
     * @param isSuccess
     * @param code: Http Status Code
     * @param message: 설명
     */
    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
