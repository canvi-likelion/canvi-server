package com.canvi.hama.domain.diary.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DiaryResponseStatus {
    /** 성공 2xx */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),
    CREATED(true, HttpStatus.CREATED.value(), "생성되었습니다."),

    /** client error - 4xx */
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST.value(), "요청 값이 옳지 않습니다."),
    NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "검색 결과가 존재하지 않습니다."),
    DIARY_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 날짜에 일기가 존재하지 않습니다."),
    DIARY_ALREADY_EXISTS(false, HttpStatus.CONFLICT.value(), "해당 날짜에 일기가 이미 존재합니다."),

    UNPROCESSABLE_ENTITY(false, HttpStatus.UNPROCESSABLE_ENTITY.value(), "API 응답 형식이 잘못되었습니다."),

    /** server error - 5xx */
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "일기 관려 처리 중 에러가 발생했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    DiaryResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
