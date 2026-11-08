package com.you.and.me.security.service.impl;

import com.you.and.me.exception.*;
import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.security.model.*;
import com.you.and.me.security.repository.AuthRepository;
import com.you.and.me.security.service.AuthService;
import com.you.and.me.security.service.JwtService;
import com.you.and.me.security.service.RefreshTokenService;
import com.you.and.me.util.CommonResponseMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public AuthServiceImpl(AuthRepository authRepository, AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public CommonResponse<String> signup(RegisterUser registerUser) {
        registerUser.setPassword(bCryptPasswordEncoder.encode(registerUser.getPassword()));
        try {
            authRepository.signup(registerUser);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    "Successfully Signup",
                    Instant.now()
            );
        } catch (UserRegisterFailedErrorExceptionHandler | InternalServerErrorExceptionHandler e) {
            throw new UserRegisterFailedErrorExceptionHandler(e.getMessage());
        }
    }

    public CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User domainUser = userDetails.getDomainUser();
            String accessToken = jwtService.generateAccessToken(domainUser);
            String refreshToken = jwtService.generateRefreshToken(domainUser);
            refreshTokenService.createRefreshToken(domainUser, refreshToken, jwtService.extractExpiration(refreshToken).toInstant());

            ResponseCookie accessCookie = jwtService.buildAccessTokenCookie(accessToken);
            ResponseCookie refreshCookie = jwtService.buildRefreshTokenCookie(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            LoginResponse loginSuccessful = LoginResponse.builder()
                    .message("Login successful")
                    .username(domainUser.getUsername())
                    .uniqueCode(generateUniqueCode())
                    .accessTokenExpiresAt(jwtService.extractExpiration(accessToken).toInstant())
                    .refreshTokenExpiresAt(jwtService.extractExpiration(refreshToken).toInstant())
                    .build();

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_CODE,
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_LOGGING_MESSAGE,
                    loginSuccessful,
                    Instant.now()
            );
        }
        throw new RuntimeException("Authentication failed");
    }

    public String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }


    @Override
    public CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<String> refreshTokenOpt = jwtService.resolveRefreshToken(request);
            refreshTokenOpt.ifPresent(refreshTokenService::revokeToken);
            ResponseCookie clearAccessCookie = jwtService.buildLogoutAccessTokenCookie();
            ResponseCookie clearRefreshCookie = jwtService.buildLogoutRefreshTokenCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, clearAccessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());
            SecurityContextHolder.clearContext();
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_LOGOUT_MESSAGE,
                    "Successfully Logout",
                    Instant.now()
            );
        } catch (Exception e) {
            return new CommonResponse<>(
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_CODE,
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_STATUS,
                    CommonResponseMessages.UNSUCCESSFULLY_LOGOUT_MESSAGE,
                    "Logout Unsuccessfully",
                    Instant.now()
            );
        }

    }

    @Override
    public CommonResponse<User> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated user");
            }
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            User user = principal.getDomainUser();
            user.setPassword(null);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    user,
                    Instant.now()
            );
        } catch (Exception e) {
            throw new DataRetrieveFailedErrorExceptionHandler("No authenticated user");
        }

    }
}
