package com.you.and.me.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertJwtTokenRequest {
    private Long userId;
    private String token;
    private Instant expiresAt;
    private String ipAddress;
    private String userAgent;
}
