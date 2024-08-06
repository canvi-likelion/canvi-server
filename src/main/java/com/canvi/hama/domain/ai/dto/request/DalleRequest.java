package com.canvi.hama.domain.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DalleRequest(
        String gender,

        String weather,

        String hairStyle,

        String clothes,

        String pictureStyle,

        @NotBlank(message = "prompt를 입력하세요.")
        String prompt
) {
}
