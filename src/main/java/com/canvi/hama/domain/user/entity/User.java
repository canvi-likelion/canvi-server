package com.canvi.hama.domain.user.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private static final int DEFAULT_CREDITS = 5;
    private static final String DEFAULT_PROFILE = "https://canvi-hama-bucket.s3.ap-northeast-2.amazonaws.com/hama_profile.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, length = 10)
    private String username;

    @NotNull
    @Column(unique = true, length = 50)
    private String email;

    @NotNull
    @Column(length = 100)
    private String password;

    @NotNull
    private int credits = DEFAULT_CREDITS;

    @Column(name = "profile")
    private String profile;

    @Column
    private String refreshToken;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String username, String password, String email, String profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = DEFAULT_PROFILE;
    }

    public static User create(String username, String email, String password) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .profile(DEFAULT_PROFILE)
                .build();
    }

    public void updatePassword(String password) { this.password = password; }

    public void updateCredits(int credits) {
        this.credits = credits;
    }

    public void updateUsername(String username) { this.username = username; }

    public void updateProfile(String profile) { this.profile = profile; }
}