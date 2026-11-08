package com.you.and.me.controller;

import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.security.model.LoginRequest;
import com.you.and.me.security.model.LoginResponse;
import com.you.and.me.security.model.RegisterUser;
import com.you.and.me.security.model.User;
import com.you.and.me.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v0/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<CommonResponse<String>> signup(@RequestBody RegisterUser registerUser) {
        CommonResponse<String> response = authService.signup(registerUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response){
        CommonResponse<LoginResponse> loginResponse = authService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<CommonResponse<String>> logout(HttpServletRequest request, HttpServletResponse response){
        CommonResponse<String> logoutResponse = authService.logout(request, response);
        return ResponseEntity.ok(logoutResponse);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<CommonResponse<User>> me(){
        LOGGER.info("Fetching current user profile");
        CommonResponse<User> user = authService.getCurrentUserProfile();
        return ResponseEntity.ok(user);
    }

}
