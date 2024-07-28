package com.canvi.hama.common.security;

import com.canvi.hama.common.security.exception.ExpiredTokenException;
import com.canvi.hama.common.security.exception.InvalidTokenException;
import com.canvi.hama.common.security.exception.TokenNotFoundException;
import com.canvi.hama.domain.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpirationInMs;
    private final long refreshTokenExpirationInMs;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                            @Value("${jwt.access-token.expiration}") long accessTokenExpirationInMs,
                            @Value("${jwt.refresh-token.expiration}") long refreshTokenExpirationInMs,
                            CustomUserDetailsService customUserDetailsService) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.accessTokenExpirationInMs = accessTokenExpirationInMs;
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), accessTokenExpirationInMs);
    }

    public String generateAccessTokenFromUsername(String username) {
        return generateToken(username, accessTokenExpirationInMs);
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), refreshTokenExpirationInMs);
    }

    private String generateToken(String username, long expirationInMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public void validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException ex) {
            throw new InvalidTokenException();
        } catch (ExpiredJwtException ex) {
            throw new ExpiredTokenException();
        } catch (IllegalArgumentException ex) {
            throw new TokenNotFoundException();
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromJWT(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpirationInMs / 1000;
    }
}
