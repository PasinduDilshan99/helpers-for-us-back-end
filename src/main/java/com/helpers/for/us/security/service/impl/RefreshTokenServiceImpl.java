package com.you.and.me.security.service.impl;

import com.you.and.me.security.model.RefreshToken;
import com.you.and.me.security.model.User;
import com.you.and.me.security.repository.RefreshTokenRepository;
import com.you.and.me.security.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken createRefreshToken(User user, String token, Instant expiryDate) {

        // Revoke all existing tokens for this user
        refreshTokenRepository.revokeAllForUser(user.getId());

        // Clean up expired tokens
//        refreshTokenRepository.deleteExpired(Instant.now());

        // Create new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(token)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

    @Override
    public void revokeToken(String token) {
        refreshTokenRepository.revokeToken(token);
    }
}
