package com.canvi.hama.common.security.exception;

import com.canvi.hama.common.response.BaseResponseStatus;

public class TokenNotFoundException extends TokenException {
    public TokenNotFoundException() {
        super(BaseResponseStatus.TOKEN_NOT_FOUND);
    }
}
