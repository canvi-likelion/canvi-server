package com.canvi.hama.common.security.exception;

import com.canvi.hama.common.response.BaseResponseStatus;

public class ExpiredTokenException extends TokenException {
    public ExpiredTokenException() {
        super(BaseResponseStatus.EXPIRED_TOKEN);
    }
}
