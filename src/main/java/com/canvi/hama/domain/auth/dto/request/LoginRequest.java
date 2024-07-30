package com.canvi.hama.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "이메일을 입력하세요.")
        String email,

        @NotBlank(message = "password를 입력하세요.")
        @Size(min = 8, message = "password는 최소 8자 이상이어야 합니다.")
        String password
) {
}
