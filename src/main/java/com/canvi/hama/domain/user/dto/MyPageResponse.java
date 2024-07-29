package com.canvi.hama.domain.user.dto;

import com.canvi.hama.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponse {
    private String profile;
    private String username;

    public MyPageResponse(User user) {
        this.profile = user.getProfile();
        this.username = user.getUsername();
    }
}
