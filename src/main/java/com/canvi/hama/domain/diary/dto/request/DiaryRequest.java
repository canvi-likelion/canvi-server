package com.canvi.hama.domain.diary.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequest {
    private String title;
    private String content;
    private LocalDate diaryDate;
}
