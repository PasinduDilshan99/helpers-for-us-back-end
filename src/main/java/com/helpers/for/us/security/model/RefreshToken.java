package com.you.and.me.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private Long id;
    private Long userId;
    private String token;
    private Instant expiryDate;
    @Builder.Default
    private boolean revoked = false;
}
