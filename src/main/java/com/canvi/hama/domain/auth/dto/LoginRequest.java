package com.canvi.hama.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "아이디가 비어있습니다.")
        String username,

        @NotBlank(message = "비밀번호가 비어있습니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String password
) {
}
