package com.canvi.hama.domain.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNameRequest(
        @NotBlank(message = "이름이 비어있습니다.")
        @Size(min = 1, message = "이름은 최소 1자 이상이어야 합니다.")
        String username
) {
}
