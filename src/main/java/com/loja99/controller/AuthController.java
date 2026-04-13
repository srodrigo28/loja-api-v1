package com.loja99.controller;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loja99.dto.request.AuthLoginRequest;
import com.loja99.dto.response.AuthSessionResponse;
import com.loja99.service.AuthService;
import com.loja99.service.AuthTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;

    @Value("${app.auth.cookie-name:loja99_auth}")
    private String authCookieName;

    @Value("${app.auth.cookie-secure:false}")
    private boolean authCookieSecure;

    @PostMapping("/login")
    public ResponseEntity<AuthSessionResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthSessionResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAuthCookie(response.token()).toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthSessionResponse> me(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @CookieValue(name = "loja99_auth", required = false) String authCookieValue
    ) {
        String token = resolveToken(authorization, authCookieValue);
        return ResponseEntity.ok(authService.getCurrentSession(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("authenticated", false);
        body.put("message", "Sessao encerrada com sucesso.");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAuthCookie().toString())
                .body(body);
    }

    private String resolveToken(String authorization, String authCookieValue) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        return authCookieValue;
    }

    private ResponseCookie buildAuthCookie(String token) {
        Duration maxAge = authTokenService.getTokenExpiration();
        return ResponseCookie.from(authCookieName, token)
                .httpOnly(true)
                .secure(authCookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    private ResponseCookie clearAuthCookie() {
        return ResponseCookie.from(authCookieName, "")
                .httpOnly(true)
                .secure(authCookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}
