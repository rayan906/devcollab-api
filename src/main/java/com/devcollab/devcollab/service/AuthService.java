package com.devcollab.devcollab.service;

import com.devcollab.devcollab.dto.request.LoginRequest;
import com.devcollab.devcollab.dto.request.RegisterRequest;
import com.devcollab.devcollab.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

}