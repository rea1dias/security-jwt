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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Авторизация", description = "Методы авторизации")
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;
    private final PasswordService passwordService;
    private final TwoFactorAuthServiceImpl twoFactorAuthService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя в системе")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
    })
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = registerService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    @Operation(summary = "Аутентификация", description = "Вход пользователя по логину и паролю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.createAuthToken(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/confirm")
    @Operation(summary = "Подтверждение email", description = "Подтверждает email пользователя по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email подтверждён"),
            @ApiResponse(responseCode = "400", description = "Неверный или истекший токен")
    })
    public ResponseEntity<String> confirm(@RequestBody ConfirmTokenRequest request) {

        boolean isRegistered = registerService.confirmToken(request);
        if (isRegistered) {
            return ResponseEntity.ok("Email подтверж    ден!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный или истекший токен!");
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Запрос на восстановление пароля", description = "Отправляет ссылку для сброса пароля на email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ссылка отправлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка обработки запроса")
    })
    public ResponseEntity<String> forgot(@RequestBody ResetPasswordRequest request) {
        try {
            passwordService.requestPasswordReset(request);
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Сброс пароля", description = "Устанавливает новый пароль по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль успешно сброшен"),
            @ApiResponse(responseCode = "400", description = "Ошибка сброса пароля")
    })
    public ResponseEntity<String> reset(@RequestBody ResetTokenRequest request) {
        try {
            passwordService.resetPassword(request);
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/enable")
    @Operation(summary = "Включение двухфакторной аутентификации", description = "Генерирует QR-код для включения 2FA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR-код успешно создан", content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)),
            @ApiResponse(responseCode = "400", description = "Ошибка включения 2FA")
    })
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
    @Operation(summary = "Валидация 2FA-кода", description = "Проверяет введённый код 2FA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код 2FA верный"),
            @ApiResponse(responseCode = "401", description = "Неверный код 2FA"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
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
