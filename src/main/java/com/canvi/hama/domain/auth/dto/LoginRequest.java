package com.canvi.hama.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디가 비어있습니다.")
        String username,

        @NotBlank(message = "비밀번호가 비어있습니다.")
        String password
) {
}
