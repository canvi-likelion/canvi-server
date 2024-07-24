package com.canvi.hama.domain.auth.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.common.util.EmailValidator;
import com.canvi.hama.common.util.RedisUtil;
import com.canvi.hama.domain.user.repository.UserRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    @Value("${spring.mail.auth-code-expiration-seconds}")
    private long expirationSeconds;

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    private void checkEmailValidation(String email) {
        if (!EmailValidator.isValidEmail(email)) {
            throw new BaseException(BaseResponseStatus.INVALID_EMAIL_FORMAT);
        }
    }

    public void sendAuthCode(String email) {
        checkEmailValidation(email);
        if (userRepository.existsByEmail(email)) {
            throw new BaseException(BaseResponseStatus.EMAIL_ALREADY_EXISTS);
        }

        String authCode = generateAuthCode();
        redisUtil.setDataExpire(email, authCode, expirationSeconds);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[hama] 이메일 인증");
        message.setText("인증 코드: " + authCode);
        mailSender.send(message);
    }

    public boolean verifyAuthCode(String email, String authCode) {
        checkEmailValidation(email);
        String storedAuthCode = redisUtil.getData(email);
        if (authCode.equals(storedAuthCode)) {
            redisUtil.setDataExpire(email + "_verified", String.valueOf(true), expirationSeconds);
            return true;
        }
        return false;
    }

    public boolean isEmailVerified(String email) {
        checkEmailValidation(email);
        Boolean isVerified = Boolean.valueOf(redisUtil.getData(email + "_verified"));
        return Boolean.TRUE.equals(isVerified);
    }

    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
