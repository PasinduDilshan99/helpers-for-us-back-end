package com.you.and.me.security.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
