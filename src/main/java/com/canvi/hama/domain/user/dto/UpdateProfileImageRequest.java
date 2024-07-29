package com.canvi.hama.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileImageRequest {
    private Long userId;
    private String profile;
}
