package com.disa.authservice.service;

import com.disa.authservice.model.auth.AuthRequest;
import com.disa.authservice.model.auth.AuthResponse;

public interface AuthService {

    AuthResponse createAuthToken(AuthRequest request);
}
