package com.you.and.me.security.service;

import com.you.and.me.security.model.CustomUserDetails;
import com.you.and.me.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<io.jsonwebtoken.Claims, T> resolver);

    boolean validateToken(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

    boolean isRefreshToken(String token);

    ResponseCookie buildAccessTokenCookie(String token);

    ResponseCookie buildRefreshTokenCookie(String token);

    ResponseCookie buildLogoutAccessTokenCookie();

    ResponseCookie buildLogoutRefreshTokenCookie();

    Optional<String> resolveAccessToken(HttpServletRequest request);

    Optional<String> resolveRefreshToken(HttpServletRequest request);

    boolean canRefresh(String refreshToken, CustomUserDetails userDetails);

    Instant getAccessExpiryInstant();

    Instant getRefreshExpiryInstant();
}
