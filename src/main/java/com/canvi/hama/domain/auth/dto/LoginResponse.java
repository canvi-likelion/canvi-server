package com.canvi.hama.domain.auth.dto;

public record LoginResponse(
        String username,
        String accessToken,
        String refreshToken
) {
}
