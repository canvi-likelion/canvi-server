package com.canvi.hama.domain.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserResponseStatus {
    SUCCESS(true,HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /** client error - 4xx */
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST.value(), "요청 값이 옳지 않습니다."),
    NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 사용자입니다."),
    NAME_ALREADY_EXIST(false, HttpStatus.CONFLICT.value(), "이미 존재하는 이름입니다."),

    UNPROCESSABLE_ENTITY(false, HttpStatus.UNPROCESSABLE_ENTITY.value(), "API 응답 형식이 잘못되었습니다."),

    /** server error - 5xx */
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "일기 관려 처리 중 에러가 발생했습니다."),
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다.")

    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    UserResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
