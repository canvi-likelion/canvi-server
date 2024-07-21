package com.canvi.hama.domain.auth.dto;

public record SignupRequest(
        String username,
        String email,
        String password
) {
}
