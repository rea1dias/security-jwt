package com.disa.authservice.service;

import com.disa.authservice.model.reset.ResetPasswordRequest;
import com.disa.authservice.model.reset.ResetTokenRequest;

public interface PasswordService {

    void requestPasswordReset(ResetPasswordRequest request);

    void resetPassword(ResetTokenRequest request);
}
