package com.canvi.hama.domain.diary.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageSaveRequest {
    private Long diaryId;
    private String imageUrl;
}
