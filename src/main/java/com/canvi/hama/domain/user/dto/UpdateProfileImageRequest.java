package com.canvi.hama.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public record UpdateProfileImageRequest(
        String profile
) {
}
