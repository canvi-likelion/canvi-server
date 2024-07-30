package com.canvi.hama.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "id를 입력하세요.")
        String username,

        @NotBlank(message = "email을 입력하세요.")
        @Email(message = "email 형식이 유효하지 않습니다.")
        String email,

        @NotBlank(message = "password를 입력하세요.")
        @Size(min = 8, message = "password는 최소 8자 이상이어야 합니다.")
        String password
) {
}
