package com.disa.authservice.service;

import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;

public interface RegisterService {

    RegisterResponse register(RegisterRequest request);

}
