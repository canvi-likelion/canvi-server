package com.canvi.hama.domain.ai.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AiResponseStatus {
    /** 성공 2xx */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /** client error - 4xx */
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST.value(), "요청 값이 옳지 않습니다."),

    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "api 처리 중 에러가 발생했습니다."),

    UNPROCESSABLE_ENTITY(false, HttpStatus.UNPROCESSABLE_ENTITY.value(), "API 응답 형식이 잘못되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    AiResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}
