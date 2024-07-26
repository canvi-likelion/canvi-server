package com.canvi.hama.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "아이디가 비어있습니다.")
        String username,

        @NotBlank(message = "이메일이 비어있습니다.")
        String email
) {
}
