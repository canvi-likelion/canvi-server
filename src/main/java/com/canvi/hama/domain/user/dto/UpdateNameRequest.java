package com.canvi.hama.domain.user.dto;

import lombok.Getter;

@Getter
public class UpdateNameRequest {
    private Long userId;
    private String username;
}
