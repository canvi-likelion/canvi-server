package com.canvi.hama.domain.user.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserException extends RuntimeException {
    private UserResponseStatus status;

    public UserException(UserResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

}
