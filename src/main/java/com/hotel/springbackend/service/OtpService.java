package com.hotel.springbackend.service;

import com.hotel.springbackend.model.OtpToken;
import com.hotel.springbackend.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    //  Generate OTP
    public String generateOtp(String email) {
        otpTokenRepository.deleteAllByEmail(email);

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtpCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);
        token.setUsed(false);

        otpTokenRepository.save(token);

        return code;
    }

    //  Verify OTP
    public void verifyOtp(String email, String code) {
        OtpToken token = otpTokenRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("No OTP found."));

        if (token.isUsed())
            throw new RuntimeException("OTP already used.");

        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired.");

        if (!token.getOtpCode().equals(code))
            throw new RuntimeException("Invalid OTP.");

        token.setVerified(true);
        otpTokenRepository.save(token);
    }

    //  Ensure verified before reset
    public void ensureVerified(String email) {
        OtpToken token = otpTokenRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP verification required."));

        if (!token.isVerified())
            throw new RuntimeException("OTP not verified.");

        if (token.isUsed())
            throw new RuntimeException("OTP already used.");
    }

    // Mark OTP as used after password reset
    public void markUsed(String email) {
       otpTokenRepository.deleteAllByEmail(email);
    }
}