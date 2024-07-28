package com.canvi.hama.domain.diary.dto;

import com.canvi.hama.domain.diary.entity.Diary;
import com.canvi.hama.domain.diary.entity.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DiarySummaryResponse {
    private String title;
    private String content;
    private Image image;

    public DiarySummaryResponse(Diary diary) {
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.image = diary.getImage();
    }

}
