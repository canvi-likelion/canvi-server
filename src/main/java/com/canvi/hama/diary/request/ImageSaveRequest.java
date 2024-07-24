package com.canvi.hama.diary.request;

import lombok.Getter;

@Getter
public class ImageSaveRequest {
    private Long diaryId;
    private String imageUrl;
}
