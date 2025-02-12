package com.disa.authservice.service;

import com.disa.authservice.model.reset.ResetTokenRequest;

public interface PasswordService {

    void requestPasswordReset(String email);

    void resetPassword(ResetTokenRequest request);
}
