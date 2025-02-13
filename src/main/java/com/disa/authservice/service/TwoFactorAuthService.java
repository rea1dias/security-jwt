package com.disa.authservice.service;

import com.disa.authservice.model.twofa.TwofaRequest;
import com.disa.authservice.model.twofa.ValidateTwofaRequest;

public interface TwoFactorAuthService {

    byte[] enableTwofaAuth(TwofaRequest request);

    boolean validateTwofaAuth(ValidateTwofaRequest request);

    boolean validateCode(String secretKey, int code);

    String generateSecretKey();

    void save2faSecret(String email, String secretKey);

    String getTwoFactorSecret(String email);
}
