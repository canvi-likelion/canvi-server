package com.canvi.hama.domain.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AiRequest(
        @NotBlank
        String username,

        @NotBlank
        String prompt
) {
}
