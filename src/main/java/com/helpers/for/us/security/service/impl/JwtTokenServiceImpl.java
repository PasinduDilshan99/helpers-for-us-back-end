package com.you.and.me.security.service.impl;

import com.you.and.me.security.model.InsertJwtTokenRequest;
import com.you.and.me.security.repository.JwtTokenRepository;
import com.you.and.me.security.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

    private final JwtTokenRepository jwtTokenRepository;

    @Autowired
    public JwtTokenServiceImpl(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    public void insertJwtToken(InsertJwtTokenRequest request) {
        jwtTokenRepository.insertJwtToken(request);
    }

}
