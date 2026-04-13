package com.loja99.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.loja99.entity.Loja;
import com.loja99.exception.AuthenticationException;

@Service
public class AuthTokenService {

    private final String tokenSecret;
    private final Duration tokenExpiration;

    public AuthTokenService(
            @Value("${app.auth.token-secret:loja99-dev-secret}") String tokenSecret,
            @Value("${app.auth.token-expiration:PT12H}") Duration tokenExpiration
    ) {
        this.tokenSecret = tokenSecret;
        this.tokenExpiration = tokenExpiration;
    }

    public String generateToken(Loja loja) {
        long expiresAt = Instant.now().plus(tokenExpiration).getEpochSecond();
        String payload = loja.getId() + "|" + loja.getOwnerEmail().trim().toLowerCase() + "|" + expiresAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String encodedSignature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(sign(payload));
        return encodedPayload + "." + encodedSignature;
    }

    public AuthTokenPayload parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("Sessao nao informada.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        byte[] receivedSignature = Base64.getUrlDecoder().decode(parts[1]);
        byte[] expectedSignature = sign(payload);

        if (!MessageDigest.isEqual(receivedSignature, expectedSignature)) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        String[] payloadParts = payload.split("\\|");
        if (payloadParts.length != 3) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        Integer storeId;
        long expiresAt;
        try {
            storeId = Integer.valueOf(payloadParts[0]);
            expiresAt = Long.parseLong(payloadParts[2]);
        } catch (NumberFormatException ex) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        if (Instant.now().getEpochSecond() >= expiresAt) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        return new AuthTokenPayload(storeId, payloadParts[1], expiresAt);
    }

    public Duration getTokenExpiration() {
        return tokenExpiration;
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Nao foi possivel assinar o token de autenticacao.", ex);
        }
    }

    public record AuthTokenPayload(Integer storeId, String ownerEmail, long expiresAt) {
    }
}
