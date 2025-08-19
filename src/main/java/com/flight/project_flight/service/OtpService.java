package com.flight.project_flight.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateOtp(String username) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6 chiffres
        otpStorage.put(username, code);
        return code;
    }

    public boolean validateOtp(String username, String code) {
        String storedCode = otpStorage.get(username);
        return storedCode != null && storedCode.equals(code);
    }
}

