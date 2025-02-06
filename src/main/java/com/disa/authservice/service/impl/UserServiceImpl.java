package com.disa.authservice.service.impl;

import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        val user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден" + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRole().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList())
        );
    }
}
