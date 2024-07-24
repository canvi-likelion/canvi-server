package com.canvi.hama.domain.auth.service;

import com.canvi.hama.common.exception.BaseException;
import com.canvi.hama.common.response.BaseResponseStatus;
import com.canvi.hama.common.security.JwtTokenProvider;
import com.canvi.hama.common.util.EmailValidator;
import com.canvi.hama.domain.auth.dto.LoginRequest;
import com.canvi.hama.domain.auth.dto.RefreshTokenResponse;
import com.canvi.hama.domain.auth.dto.SignupRequest;
import com.canvi.hama.domain.auth.dto.TokenResponse;
import com.canvi.hama.domain.user.domain.User;
import com.canvi.hama.domain.user.repository.UserRepository;
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

    private void checkEmailValidation(String email) {
        if (!EmailValidator.isValidEmail(email)) {
            throw new BaseException(BaseResponseStatus.INVALID_EMAIL_FORMAT);
        }
    }

    @Transactional
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Transactional
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
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

            userRepository.findByUsername(loginRequest.username())
                    .ifPresent(user -> {
                        user.updateRefreshToken(refreshToken);
                        userRepository.save(user);
                    });

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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        user.clearRefreshToken();
        userRepository.save(user);
    }

    @Transactional
    public RefreshTokenResponse generateNewAccessTokenFromRefreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        tokenProvider.validateToken(refreshToken);
        String username = tokenProvider.getUsernameFromJWT(refreshToken);

        String finalRefreshToken = refreshToken;
        return userRepository.findByUsername(username)
                .filter(user -> {
                    assert finalRefreshToken != null;
                    return finalRefreshToken.equals(user.getRefreshToken());
                })
                .map(user -> {
                    String newAccessToken = tokenProvider.generateAccessTokenFromUsername(username);
                    return new RefreshTokenResponse(newAccessToken);
                })
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INVALID_TOKEN));
    }
}
