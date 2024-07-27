package com.canvi.hama.domain.diary.dto;

import com.canvi.hama.domain.diary.entity.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiarySummaryResponse {
    private String title;
    private String content;

    public DiarySummaryResponse(Diary diary) {
        this.title = diary.getTitle();
        this.content = diary.getContent();
    }

}
