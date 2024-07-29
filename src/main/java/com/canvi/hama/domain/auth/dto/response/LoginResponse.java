package com.canvi.hama.domain.auth.dto.response;

public record LoginResponse(
        String username,
        String accessToken,
        String refreshToken
) {
}
