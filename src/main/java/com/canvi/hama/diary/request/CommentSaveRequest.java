package com.canvi.hama.diary.request;

import lombok.Getter;

@Getter
public class CommentSaveRequest {
    private Long diaryId;
    private Long userId;
    private String comment;
}
