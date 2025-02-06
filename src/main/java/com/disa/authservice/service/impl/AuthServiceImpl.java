package com.disa.authservice.service.impl;

import com.disa.authservice.model.auth.AuthRequest;
import com.disa.authservice.model.auth.AuthResponse;
import com.disa.authservice.service.AuthService;
import com.disa.authservice.service.UserService;
import com.disa.authservice.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager manager;
    private final JwtTokenUtils utils;
    private final UserService userService;

    @Override
    public AuthResponse createAuthToken(AuthRequest request) {

        val authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        manager.authenticate(authentication);

        val token = utils.generateToken(userService.loadUserByUsername(request.getUsername()));
        return new AuthResponse(token);
    }
}
