package com.canvi.hama.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
