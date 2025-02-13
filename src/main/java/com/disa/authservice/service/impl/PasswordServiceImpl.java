package com.disa.authservice.service.impl;

import com.disa.authservice.entity.ResetToken;
import com.disa.authservice.entity.User;
import com.disa.authservice.model.reset.ResetPasswordRequest;
import com.disa.authservice.model.reset.ResetTokenRequest;
import com.disa.authservice.repo.ResetTokenRepository;
import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.EmailService;
import com.disa.authservice.service.PasswordService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final ResetTokenRepository repository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    @Override
    public void requestPasswordReset(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String tokenReset = UUID.randomUUID().toString();
        ResetToken token = ResetToken.builder()
                .token(tokenReset)
                .user(user)
                .createdTime(LocalDateTime.now())
                .expiredTime(LocalDateTime.now().plusMinutes(30))
                .build();
        repository.save(token);

        String resetLink = "http://localhost:8080/auth/forgot-password?token=" + tokenReset;

        String subject = "Сброс пароля";
        String body = "<p>Здравствуйте, " + user.getFirstName() + "!</p>"
                + "<p>Вы запросили сброс пароля. Перейдите по ссылке ниже, чтобы установить новый пароль:</p>"
                + "<a href=\"" + resetLink + "\">Сбросить пароль</a>";

        try {
            emailService.sendEmail(user.getEmail(), subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }
    }

    @Override
    public void resetPassword(ResetTokenRequest request) {

        ResetToken resetToken = repository.findByToken(request.getToken())
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));

        if (resetToken.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new UsernameNotFoundException("Token is expired");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));

        userRepository.save(user);

        repository.delete(resetToken);
    }
}
