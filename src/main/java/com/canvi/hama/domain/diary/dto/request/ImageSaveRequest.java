package com.canvi.hama.domain.diary.dto.request;


import jakarta.validation.constraints.NotBlank;

public record ImageSaveRequest(
        @NotBlank(message = "imageUrl을 입력하세요.")
        String imageUrl
) {
}
