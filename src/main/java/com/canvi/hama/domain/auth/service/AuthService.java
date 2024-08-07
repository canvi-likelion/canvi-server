package com.canvi.hama.domain.auth.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.common.util.RedisUtil;
import com.canvi.hama.domain.auth.dto.request.LoginRequest;
import com.canvi.hama.domain.auth.dto.request.ResetPasswordRequest;
import com.canvi.hama.domain.auth.dto.request.SignupRequest;
import com.canvi.hama.domain.auth.dto.response.LoginResponse;
import com.canvi.hama.domain.auth.dto.response.RefreshTokenResponse;
import com.canvi.hama.domain.user.entity.User;
import com.canvi.hama.domain.user.repository.UserRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService;
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username())) {
            throw new BaseException(BaseResponseStatus.USERNAME_ALREADY_EXISTS);
        }

        if (!isEmailAvailable(signUpRequest.email())) {
            throw new BaseException(BaseResponseStatus.EMAIL_ALREADY_EXISTS);
        }

        if (!emailAuthService.isEmailVerified(signUpRequest.email())) {
            throw new BaseException(BaseResponseStatus.EMAIL_NOT_VERIFIED);
        }

        User user = User.create(
                signUpRequest.username(),
                signUpRequest.email(),
                passwordEncoder.encode(signUpRequest.password())
        );
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            redisUtil.setDataExpire(loginRequest.email(), refreshToken,
                    tokenProvider.getRefreshTokenExpirationInSeconds());

            String username = userRepository.findByEmail(email)
                    .map(User::getUsername)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

            return new LoginResponse(username, email, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BaseException(BaseResponseStatus.INVALID_CREDENTIALS);
        }
    }

    public void logoutUser(String email) {
        redisUtil.deleteData(email);
    }

    public RefreshTokenResponse generateNewAccessToken(String email) {
        String storedRefreshToken = redisUtil.getData(email);
        if (storedRefreshToken == null) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        String newAccessToken = tokenProvider.generateAccessTokenFromUsername(email);
        return new RefreshTokenResponse(newAccessToken);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        String newPassword = generateRandomPassword();
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailAuthService.sendNewPassword(user.getEmail(), newPassword);
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        redisUtil.deleteData(email);
        userRepository.delete(user);
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
