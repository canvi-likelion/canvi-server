package com.canvi.hama.domain.diary.dto.response;

import com.canvi.hama.domain.diary.entity.Diary;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record DiaryGetResponse(Long id, String title, String content, CommentGetResponse comment, ImageGetResponse image, LocalDate date) {

    public DiaryGetResponse(Diary diary) {
        this(diary.getId(), diary.getTitle(), diary.getContent(),
                diary.getComment() != null ? new CommentGetResponse(diary.getComment()) : null,
                diary.getImage() != null ? new ImageGetResponse(diary.getImage()) : null,
                diary.getDiaryDate());
    }

    public static List<DiaryGetResponse> fromDiaryList(List<Diary> diaries) {
        return diaries.stream()
                .map(DiaryGetResponse::new)
                .collect(Collectors.toList());
    }
}
