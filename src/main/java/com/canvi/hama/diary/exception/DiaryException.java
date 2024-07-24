package com.canvi.hama.diary.exception;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.diary.response.DiaryResponseStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class DiaryException extends RuntimeException {
    private DiaryResponseStatus status;

    public DiaryException(DiaryResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

}
