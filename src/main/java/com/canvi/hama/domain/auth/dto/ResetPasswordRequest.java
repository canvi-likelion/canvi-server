package com.canvi.hama.domain.auth.dto;

public record ResetPasswordRequest(
        String username,
        String email
) {
}
