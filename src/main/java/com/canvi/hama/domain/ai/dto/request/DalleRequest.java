package com.canvi.hama.domain.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DalleRequest(
        @NotBlank
        Long diaryId,

        @NotBlank
        String gender,

        @NotBlank
        String age,

        @NotBlank
        String hairStyle,

        @NotBlank
        String clothes,

        @NotBlank
        String prompt
) {
}
