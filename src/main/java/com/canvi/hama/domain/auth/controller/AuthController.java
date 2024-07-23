package com.canvi.hama.domain.auth.controller;

import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.RefreshTokenResponse;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.auth.dto.TokenResponse;
import com.canvi.hama.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> registerUser(@RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new BaseResponse<>(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logoutUser(@RequestHeader("Authorization") String accessToken) {
        authService.logoutUser(accessToken);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshAccessToken(
            @RequestHeader("Authorization") String refreshToken) {
        RefreshTokenResponse refreshTokenResponse = authService.generateNewAccessTokenFromRefreshToken(refreshToken);
        return ResponseEntity.ok(new BaseResponse<>(refreshTokenResponse));
    }
}
