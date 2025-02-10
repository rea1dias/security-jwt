package com.disa.authservice.service.impl;

import com.disa.authservice.entity.ConfirmToken;
import com.disa.authservice.entity.User;
import com.disa.authservice.enums.Role;
import com.disa.authservice.exception.UserAlreadyExistsException;
import com.disa.authservice.mapper.RegisterMapper;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import com.disa.authservice.repo.ConfirmTokenRepository;
import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.RegisterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final RegisterMapper mapper;
    private final ConfirmTokenRepository confirmTokenRepository;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        val user = mapper.toEntity(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setRole(List.of(Role.USER));
        User saved = repository.save(user);

        createConfirmToken(saved);

        // TODO: Send confirm token
        return mapper.toResponse(saved);
    }


    @Override
    public void createConfirmToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmToken confirmToken = ConfirmToken.builder()
                .token(token)
                .user(user)
                .createdTime(LocalDateTime.now())
                .expiredTime(LocalDateTime.now().plusMinutes(20))
                .confirmedTime(LocalDateTime.now())
                .build();
        confirmTokenRepository.save(confirmToken);
    }
}
