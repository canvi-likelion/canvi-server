package com.canvi.hama.domain.diary.dto.response;

import com.canvi.hama.domain.diary.entity.Image;

public record ImageGetResponse(Long id, String url) {
    public ImageGetResponse(Image image) {
        this(image != null ? image.getId() : null,
                image != null ? image.getUrl() : null);
    }
}
