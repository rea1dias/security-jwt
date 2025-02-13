package com.disa.authservice.service.impl;

import com.disa.authservice.entity.User;
import com.disa.authservice.model.twofa.TwofaRequest;
import com.disa.authservice.model.twofa.ValidateTwofaRequest;
import com.disa.authservice.repo.UserRepository;
import com.disa.authservice.service.TwoFactorAuthService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    private final UserRepository userRepository;

    @Override
    public byte[] enableTwofaAuth(TwofaRequest request) {
        try {
            String secretKey = generateSecretKey();
            save2faSecret(request.getEmail(), secretKey);
            String qrCodeUrl = generateQRCodeUrl(secretKey, request.getEmail(), "E-learn");


            return generateQRCodeImage(qrCodeUrl);
        } catch (WriterException e) {
            throw new RuntimeException("Failed to generate QR Code", e);
        }
    }

    @Override
    public boolean validateTwofaAuth(ValidateTwofaRequest request) {
        String secretKey = getTwoFactorSecret(request.getEmail());
        return googleAuthenticator.authorize(secretKey, request.getCode());
    }

    @Override
    public boolean validateCode(String secretKey, int code) {
        return googleAuthenticator.authorize(secretKey, code);
    }

    /**
     * Generate a secret key for a user.
     */
    @Override
    public String generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    /**
     * Generate a QR Code URL for Google Authenticator.
     *
     * @param secretKey The user's secret key.
     * @param account   The account name (usually the user's email).
     * @param issuer    The name of your application.
     */
    public String generateQRCodeUrl(String secretKey, String account, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer,
                account,
                secretKey,
                issuer);
    }

    /**
     * Generate a QR Code image as Base64.
     *
     * @param qrCodeUrl The QR Code URL.
     */
    public byte[] generateQRCodeImage(String qrCodeUrl) throws WriterException {
        int width = 250;
        int height = 250;
        BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter()
                .encode(qrCodeUrl,
                        BarcodeFormat.QR_CODE,
                        width,
                        height);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR Code image", e);
        }
    }

    @Override
    public void save2faSecret(String email,
                              String secretKey) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Override
    public String getTwoFactorSecret(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getTwoFactorSecret();
    }
}
