package com.canvi.hama.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(
        @NotBlank(message = "email을 입력하세요.")
        @Email(message = "email 형식이 유효하지 않습니다.")
        String email,

        @NotBlank(message = "code를 입력하세요.")
        String code
) {
}