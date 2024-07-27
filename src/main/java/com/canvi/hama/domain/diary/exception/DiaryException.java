package com.canvi.hama.domain.diary.exception;

import com.canvi.hama.domain.diary.response.DiaryResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryException extends RuntimeException {
    private DiaryResponseStatus status;

    public DiaryException(DiaryResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

}
