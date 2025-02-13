package com.disa.authservice.service.impl;

import com.disa.authservice.entity.ConfirmToken;
import com.disa.authservice.entity.User;
import com.disa.authservice.enums.Role;
import com.disa.authservice.exception.UserAlreadyExistsException;
import com.disa.authservice.mapper.RegisterMapper;
import com.disa.authservice.model.confirmToken.ConfirmTokenRequest;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import com.disa.authservice.repo.ConfirmTokenRepository;
import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.EmailService;
import com.disa.authservice.service.RegisterService;
import com.disa.authservice.service.TwoFactorAuthService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final RegisterMapper mapper;
    private final ConfirmTokenRepository confirmTokenRepository;
    private final EmailService emailService;
    private final TwoFactorAuthService twoFactorAuthService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        val user = mapper.toEntity(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setTwoFactorSecret(twoFactorAuthService.generateSecretKey());
        user.setTwoFactorEnabled(false);
        user.setRole(List.of(Role.USER));
        User saved = repository.save(user);

        String token = createConfirmToken(saved);
        sendConfirmationEmail(saved, token);

        return mapper.toResponse(saved);
    }

    private void sendConfirmationEmail(User user, String token) {
        String subject = "Подтвердите ваш email";
        String confirmationLink = "http://localhost:8080/auth/confirm?token=" + token;
        String text = "<h3>Добро пожаловать, " + user.getFirstName() + "!</h3>"
                + "<p>Для завершения регистрации, подтвердите вашу почту, нажав на ссылку ниже:</p>"
                + "<a href=\"" + confirmationLink + "\">Подтвердить Email</a>";

        try {
            emailService.sendEmail(user.getEmail(), subject, text);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String createConfirmToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmToken confirmToken = ConfirmToken.builder()
                .token(token)
                .user(user)
                .createdTime(LocalDateTime.now())
                .expiredTime(LocalDateTime.now().plusMinutes(20))
                .confirmedTime(LocalDateTime.now())
                .build();
        confirmTokenRepository.save(confirmToken);
        return token;
    }

    @Override
    public boolean confirmToken(ConfirmTokenRequest request) {

        Optional<ConfirmToken> optionalToken = confirmTokenRepository.findByToken(request.getToken());

        if (optionalToken.isEmpty()) {
            return false;
        }

        ConfirmToken confirmToken = optionalToken.get();

        if (confirmToken.getExpiredTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = confirmToken.getUser();
        user.setEnabled(true);
        repository.save(user);

        confirmTokenRepository.delete(confirmToken);
        return true;
    }

    @Override
    public String resetPassword(String email) {

        return "";
    }
}
