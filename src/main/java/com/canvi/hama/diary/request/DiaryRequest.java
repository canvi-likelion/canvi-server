package com.canvi.hama.diary.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiaryRequest {
    private Integer userId;
    private String title;
    private String content;
    private LocalDate diaryDate;
}
