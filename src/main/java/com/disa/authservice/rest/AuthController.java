package com.disa.authservice.rest;

import com.disa.authservice.model.auth.AuthRequest;
import com.disa.authservice.model.auth.AuthResponse;
import com.disa.authservice.model.confirmToken.ConfirmTokenRequest;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import com.disa.authservice.model.reset.ResetPasswordRequest;
import com.disa.authservice.model.reset.ResetTokenRequest;
import com.disa.authservice.model.twofa.TwofaRequest;
import com.disa.authservice.model.twofa.ValidateTwofaRequest;
import com.disa.authservice.service.AuthService;
import com.disa.authservice.service.PasswordService;
import com.disa.authservice.service.RegisterService;
import com.disa.authservice.service.impl.TwoFactorAuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;
    private final PasswordService passwordService;
    private final TwoFactorAuthServiceImpl twoFactorAuthService;

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
    public ResponseEntity<String> confirm(@RequestBody ConfirmTokenRequest request) {

        boolean isRegistered = registerService.confirmToken(request);
        if (isRegistered) {
            return ResponseEntity.ok("Email подтверж    ден!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный или истекший токен!");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgot(@RequestBody ResetPasswordRequest request) {
        try {
            passwordService.requestPasswordReset(request);
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> reset(@RequestBody ResetTokenRequest request) {
        try {
            passwordService.resetPassword(request);
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/enable")
    public ResponseEntity<byte[]> enable2fa(@RequestBody TwofaRequest request) {
        try {
            byte[] qrCode = twoFactorAuthService.enableTwofaAuth(request);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(("Failed to enable 2FA: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate2fa(@RequestBody ValidateTwofaRequest request) {
        try {
            if (twoFactorAuthService.validateTwofaAuth(request)) {
                return ResponseEntity.ok("2FA code is valid.");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid code");
        }

    }


}
