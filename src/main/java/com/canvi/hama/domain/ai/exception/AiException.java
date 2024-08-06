package com.canvi.hama.domain.ai.exception;

import com.canvi.hama.domain.ai.enums.AiResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiException extends RuntimeException {
    private AiResponseStatus status;

    public AiException(AiResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
