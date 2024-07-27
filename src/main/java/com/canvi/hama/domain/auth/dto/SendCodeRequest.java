package com.canvi.hama.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendCodeRequest(
        @NotBlank(message = "이메일이 비었습니다.")
        @Email(message = "이메일 형식이 유효하지 않습니다.")
        String email
) {
}
