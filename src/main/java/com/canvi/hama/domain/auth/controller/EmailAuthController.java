package com.canvi.hama.domain.auth.controller;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponse;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.domain.auth.service.AuthService;
import com.canvi.hama.domain.auth.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email-auth")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;
    private final AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<BaseResponse<Void>> sendAuthCode(@RequestParam String email) {
        if (!authService.isEmailAvailable(email)) {
            throw new BaseException(BaseResponseStatus.EMAIL_ALREADY_EXISTS);
        }
        emailAuthService.sendAuthCode(email);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<BaseResponse<Void>> verifyAuthCode(@RequestParam String email, @RequestParam String code) {
        if (emailAuthService.verifyAuthCode(email, code)) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
        } else {
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_CODE);
        }
    }
}
