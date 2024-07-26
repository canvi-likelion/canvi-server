package com.canvi.hama.domain.user.entity;

import com.canvi.hama.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private static final int DEFAULT_CREDITS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 10)
    private String username;

    @NotNull
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotNull
    @Column(nullable = false, length = 100)
    private String password;

    @NotNull
    @Column(nullable = false)
    private int credits = DEFAULT_CREDITS;

    @Column
    private String refreshToken;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static User create(String username, String email, String password) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }

    public void updatePassword(String password) { this.password = password; }

    public void updateCredits(int credits) {
        this.credits = credits;
    }
}
