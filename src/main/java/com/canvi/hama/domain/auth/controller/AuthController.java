package com.canvi.hama.domain.auth.controller;

import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.auth.dto.request.LoginRequest;
import com.canvi.hama.domain.auth.dto.response.LoginResponse;
import com.canvi.hama.domain.auth.dto.response.RefreshTokenResponse;
import com.canvi.hama.domain.auth.dto.request.ResetPasswordRequest;
import com.canvi.hama.domain.auth.dto.request.SignupRequest;
import com.canvi.hama.domain.auth.service.AuthService;
import com.canvi.hama.domain.auth.swagger.RefreshAccessTokenApi;
import com.canvi.hama.domain.auth.swagger.UserAuthenticateApi;
import com.canvi.hama.domain.auth.swagger.UserLogoutApi;
import com.canvi.hama.domain.auth.swagger.UserRegisterApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @UserRegisterApi
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @UserAuthenticateApi
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new BaseResponse<>(loginResponse));
    }

    @UserLogoutApi
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logoutUser(userDetails.getUsername());
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @RefreshAccessTokenApi
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshAccessToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        RefreshTokenResponse refreshTokenResponse = authService.generateNewAccessToken(
                userDetails.getUsername());
        return ResponseEntity.ok(new BaseResponse<>(refreshTokenResponse));
    }

    @GetMapping("/find-username")
    public ResponseEntity<BaseResponse<String>> findUsernameByEmail(
            @Valid @RequestParam @NotBlank(message = "이메일이 비었습니다.") @Email(message = "이메일 형식이 유효하지 않습니다.") String email) {
        String username = authService.findUsernameByEmail(email);
        return ResponseEntity.ok(new BaseResponse<>(username));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @DeleteMapping("/user")
    public ResponseEntity<BaseResponse<Void>> withdrawUser(@AuthenticationPrincipal UserDetails userDetails) {
        authService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }
}
