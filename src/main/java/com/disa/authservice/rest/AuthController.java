package com.disa.authservice.rest;

import com.disa.authservice.model.auth.AuthRequest;
import com.disa.authservice.model.auth.AuthResponse;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import com.disa.authservice.service.AuthService;
import com.disa.authservice.service.EmailService;
import com.disa.authservice.service.RegisterService;
import com.disa.authservice.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;
    private final EmailService emailService;
    private final EmailServiceImpl emailServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = registerService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.createAuthToken(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {

        boolean isRegistered = registerService.confirmToken(token);
        if (isRegistered) {
            return ResponseEntity.ok("Email подтвержден!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный или истекший токен!");
        }

    }




}
