package com.canvi.hama.domain.auth.controller;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.auth.dto.SendCodeRequest;
import com.canvi.hama.domain.auth.dto.VerifyCodeRequest;
import com.canvi.hama.domain.auth.service.AuthService;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import com.canvi.hama.domain.auth.swagger.SendAuthCodeApi;
import com.canvi.hama.domain.auth.swagger.VerifyAuthCodeApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email Auth")
@RestController
@RequestMapping("/api/email-auth")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;
    private final AuthService authService;

    @SendAuthCodeApi
    @PostMapping("/send-code")
    public ResponseEntity<BaseResponse<Void>> sendAuthCode(@Valid @RequestBody SendCodeRequest sendCodeRequest) {
        String email = sendCodeRequest.email();
        if (!authService.isEmailAvailable(email)) {
            throw new BaseException(BaseResponseStatus.EMAIL_ALREADY_EXISTS);
        }
        emailAuthService.sendAuthCode(email);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @VerifyAuthCodeApi
    @PostMapping("/verify-code")
    public ResponseEntity<BaseResponse<Void>> verifyAuthCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        if (emailAuthService.verifyAuthCode(verifyCodeRequest.email(), verifyCodeRequest.code())) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
        } else {
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_CODE);
        }
    }
}
