package com.canvi.hama.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendCodeRequest(
        @NotBlank(message = "email을 입력하세요.")
        @Email(message = "email 형식이 유효하지 않습니다.")
        String email
) {
}
