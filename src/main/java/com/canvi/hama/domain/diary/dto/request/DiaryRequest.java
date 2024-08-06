package com.canvi.hama.domain.diary.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record DiaryRequest(
        @NotBlank(message = "title을 입력하세요.")
        String title,

        @NotBlank(message = "content를 입력하세요.")
        String content,

        @NotNull(message = "diaryDate를 입력하세요.")
        @PastOrPresent(message = "diaryDate는 과거 또는 오늘 날짜여야 합니다.")
        LocalDate diaryDate
) {
}
