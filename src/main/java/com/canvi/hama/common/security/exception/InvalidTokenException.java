package com.canvi.hama.common.security.exception;

import com.canvi.hama.common.response.BaseResponseStatus;

public class InvalidTokenException extends TokenException {
    public InvalidTokenException() {
        super(BaseResponseStatus.INVALID_TOKEN);
    }
}
