package com.you.and.me.security.service;


import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.security.model.LoginRequest;
import com.you.and.me.security.model.LoginResponse;
import com.you.and.me.security.model.RegisterUser;
import com.you.and.me.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    CommonResponse<String> signup(RegisterUser registerUser);

    CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response);

    CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

    CommonResponse<User> getCurrentUserProfile();

}
