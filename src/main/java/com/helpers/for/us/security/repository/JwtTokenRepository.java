package com.you.and.me.security.repository;

import com.you.and.me.security.model.InsertJwtTokenRequest;

public interface JwtTokenRepository {
    void insertJwtToken(InsertJwtTokenRequest request);
}
