package com.you.and.me.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LoginResponse {
    private String message;
    private String username;
    private String uniqueCode;
    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;
}
