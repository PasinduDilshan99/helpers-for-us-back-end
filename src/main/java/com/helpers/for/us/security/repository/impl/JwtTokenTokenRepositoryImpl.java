package com.you.and.me.security.repository.impl;

import com.you.and.me.security.model.InsertJwtTokenRequest;
import com.you.and.me.security.repository.JwtTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class JwtTokenTokenRepositoryImpl implements JwtTokenRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenTokenRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JwtTokenTokenRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void insertJwtToken(InsertJwtTokenRequest request) {
        String sql = "INSERT INTO jwt_tokens (user_id, token, expires_at, ip_address, user_agent) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql,
                request.getUserId(),
                request.getToken(),
                Timestamp.from(request.getExpiresAt()),
                request.getIpAddress(),
                request.getUserAgent()
        );
    }
}
