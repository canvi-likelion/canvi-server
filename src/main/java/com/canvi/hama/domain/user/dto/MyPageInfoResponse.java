package com.canvi.hama.domain.user.dto;

import com.canvi.hama.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageInfoResponse {
    private String username;
    private String email;

    public MyPageInfoResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
