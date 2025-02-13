package com.disa.authservice.service;

import com.disa.authservice.entity.User;
import com.disa.authservice.model.confirmToken.ConfirmTokenRequest;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;

public interface RegisterService {

    RegisterResponse register(RegisterRequest request);

    String createConfirmToken(User user);

    boolean confirmToken(ConfirmTokenRequest request);

    String resetPassword(String email);

}
