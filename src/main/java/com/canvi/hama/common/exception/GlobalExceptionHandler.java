package com.canvi.hama.common.exception;

import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.diary.exception.DiaryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException ex) {
        return ResponseEntity.status(ex.getStatus().getCode())
                .body(new BaseResponse<>(ex.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(DiaryException.class)
    public ResponseEntity<BaseResponse<?>> handleDiaryException(DiaryException ex) {
        return ResponseEntity.status(ex.getStatus().getCode())
                .body(new BaseResponse<>(ex.getStatus().isSuccess(), ex.getStatus().getCode(), ex.getStatus().getMessage()));
    }
}
