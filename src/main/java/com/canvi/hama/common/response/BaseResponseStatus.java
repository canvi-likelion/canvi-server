package com.canvi.hama.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /** 성공 2xx */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /** client error - 4xx */
    NON_EXIST_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),

    /** server error - 5xx */
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),

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