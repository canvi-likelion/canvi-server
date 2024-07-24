package com.canvi.hama.domain.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
