package com.canvi.hama.domain.diary.dto.response;

import java.util.List;

public record DiaryGetListResponse(List<DiaryGetResponse> diaryGetResponseList) {
    public DiaryGetListResponse(List<DiaryGetResponse> diaryGetResponseList) {
        this.diaryGetResponseList = diaryGetResponseList;
    }
}
