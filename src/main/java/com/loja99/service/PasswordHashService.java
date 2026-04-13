package com.loja99.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

@Service
public class PasswordHashService {

    public String hash(String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(rawPassword.trim().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o hash da senha.", ex);
        }
    }

    public boolean matches(String rawPassword, String expectedHash) {
        return hash(rawPassword).equals(expectedHash);
    }
}
