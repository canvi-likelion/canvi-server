package com.canvi.hama.domain.diary.dto.request;


import jakarta.validation.constraints.NotBlank;

public record CommentSaveRequest(
        @NotBlank(message = "comment를 입력하세요.")
        String comment
) {
}
