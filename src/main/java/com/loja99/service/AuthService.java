package com.loja99.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loja99.dto.request.AuthLoginRequest;
import com.loja99.dto.response.AuthSessionResponse;
import com.loja99.dto.response.AuthStoreResponse;
import com.loja99.dto.response.AuthUserResponse;
import com.loja99.entity.Loja;
import com.loja99.exception.AuthenticationException;
import com.loja99.repository.LojaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LojaRepository lojaRepository;
    private final PasswordHashService passwordHashService;
    private final AuthTokenService authTokenService;

    @Transactional(readOnly = true)
    public AuthSessionResponse login(AuthLoginRequest request) {
        Loja loja = lojaRepository.findByOwnerEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Email ou senha invalidos."));

        if (!passwordHashService.matches(request.password(), loja.getPasswordHash())) {
            throw new AuthenticationException("Email ou senha invalidos.");
        }

        String token = authTokenService.generateToken(loja);
        return buildSessionResponse(loja, token);
    }

    @Transactional(readOnly = true)
    public AuthSessionResponse getCurrentSession(String token) {
        AuthTokenService.AuthTokenPayload payload = authTokenService.parseToken(token);
        Loja loja = lojaRepository.findById(payload.storeId())
                .orElseThrow(() -> new AuthenticationException("Sessao invalida ou expirada."));

        if (!loja.getOwnerEmail().equalsIgnoreCase(payload.ownerEmail())) {
            throw new AuthenticationException("Sessao invalida ou expirada.");
        }

        return buildSessionResponse(loja, token);
    }

    private AuthSessionResponse buildSessionResponse(Loja loja, String token) {
        return AuthSessionResponse.builder()
                .authenticated(true)
                .token(token)
                .user(AuthUserResponse.builder()
                        .role("lojista")
                        .ownerName(loja.getOwnerName())
                        .ownerEmail(loja.getOwnerEmail())
                        .build())
                .store(AuthStoreResponse.builder()
                        .id(loja.getId())
                        .name(loja.getName())
                        .slug(loja.getSlug())
                        .ownerEmail(loja.getOwnerEmail())
                        .status(loja.getStatus())
                        .build())
                .build();
    }
}
