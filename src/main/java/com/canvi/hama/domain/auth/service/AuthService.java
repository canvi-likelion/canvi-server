package com.canvi.hama.domain.auth.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.common.util.EmailValidator;
import com.canvi.hama.common.util.RedisUtil;
import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.RefreshTokenResponse;
import com.canvi.hama.domain.auth.dto.ResetPasswordRequest;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.auth.dto.TokenResponse;
import com.canvi.hama.domain.user.dto.User;
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

    private void checkEmailValidation(String email) {
        if (!EmailValidator.isValidEmail(email)) {
            throw new BaseException(BaseResponseStatus.INVALID_EMAIL_FORMAT);
        }
    }

    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return userRepository.countByUsername(username) == 0;
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return userRepository.countByEmail(email) == 0;
    }

    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        checkEmailValidation(signUpRequest.email());

        if (!isUsernameAvailable(signUpRequest.username())) {
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
    public TokenResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            redisUtil.setDataExpire(loginRequest.username(), refreshToken, tokenProvider.getRefreshTokenExpirationInSeconds());

            return new TokenResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BaseException(BaseResponseStatus.INVALID_CREDENTIALS);
        }
    }

    @Transactional
    public void logoutUser(String accessToken) {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        accessToken = accessToken.substring(7);
        String username = tokenProvider.getUsernameFromJWT(accessToken);
        redisUtil.deleteData(username);
    }

    @Transactional
    public RefreshTokenResponse generateNewAccessTokenFromRefreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        tokenProvider.validateToken(refreshToken);
        String username = tokenProvider.getUsernameFromJWT(refreshToken);

        String storedRefreshToken = redisUtil.getData(username);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        String newAccessToken = tokenProvider.generateAccessTokenFromUsername(username);
        return new RefreshTokenResponse(newAccessToken);
    }

    @Transactional(readOnly = true)
    public String findUsernameByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));
        return user.getUsername();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsernameAndEmail(request.username(), request.email())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INVALID_FIND_USERNAME_REQUEST));

        String newPassword = generateRandomPassword();
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailAuthService.sendNewPassword(user.getEmail(), newPassword);
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
