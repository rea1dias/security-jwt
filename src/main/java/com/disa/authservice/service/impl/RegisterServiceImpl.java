package com.disa.authservice.service.impl;

import com.disa.authservice.entity.User;
import com.disa.authservice.enums.Role;
import com.disa.authservice.exception.UserAlreadyExistsException;
import com.disa.authservice.mapper.RegisterMapper;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final RegisterMapper mapper;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        val user = mapper.toEntity(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setRole(List.of(Role.USER));
        return mapper.toResponse(repository.save(user));

    }
}
