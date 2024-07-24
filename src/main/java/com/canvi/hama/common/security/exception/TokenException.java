package com.canvi.hama.common.security.exception;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;

public class TokenException extends BaseException {
    public TokenException(BaseResponseStatus status) {
        super(status);
    }
}
