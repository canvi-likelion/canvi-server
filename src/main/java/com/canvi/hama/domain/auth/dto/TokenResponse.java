package com.canvi.hama.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
