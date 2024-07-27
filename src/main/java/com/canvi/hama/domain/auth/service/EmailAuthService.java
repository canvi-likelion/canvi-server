package com.canvi.hama.domain.auth.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
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

    @Value("${spring.mail.resend-limit-seconds}")
    private long resendLimitSeconds;

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    public void sendAuthCode(String email) {
        String lastSentKey = email + "_last_sent";
        String lastSentTime = redisUtil.getData(lastSentKey);

        if (lastSentTime != null) {
            long timeSinceLastSent = System.currentTimeMillis() - Long.parseLong(lastSentTime);
            if (timeSinceLastSent < resendLimitSeconds * 1000) {
                throw new BaseException(BaseResponseStatus.EMAIL_RESEND_TOO_SOON);
            }
        }

        String authCode = generateAuthCode();
        redisUtil.setDataExpire(email, authCode, expirationSeconds);
        redisUtil.setDataExpire(lastSentKey, String.valueOf(System.currentTimeMillis()), resendLimitSeconds);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[hama] 이메일 인증");
        message.setText("인증 코드: " + authCode);
        mailSender.send(message);
    }

    public boolean verifyAuthCode(String email, String authCode) {
        String storedAuthCode = redisUtil.getData(email);
        if (authCode.equals(storedAuthCode)) {
            redisUtil.setDataExpire(email + "_verified", String.valueOf(true), expirationSeconds);
            return true;
        }
        return false;
    }

    public boolean isEmailVerified(String email) {
        Boolean isVerified = Boolean.valueOf(redisUtil.getData(email + "_verified"));
        return Boolean.TRUE.equals(isVerified);
    }

    public void sendNewPassword(String email, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[hama] 새 비밀번호 안내");
        message.setText("귀하의 새로운 비밀번호는 " + newPassword + " 입니다. 로그인 후 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }

    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
