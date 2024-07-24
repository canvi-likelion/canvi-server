package com.canvi.hama.diary.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiaryRequest {
    private Long userId;
    private String title;
    private String content;
    private LocalDate diaryDate;

    public DiaryRequest(Long testUserId, String testTitle, String testContent, LocalDate now) {
    }
}
