package com.saasplatform.auth.controller;

import com.saasplatform.auth.dto.LoginRequest;
import com.saasplatform.auth.dto.LoginResponse;
import com.saasplatform.auth.dto.RefreshTokenRequest;
import com.saasplatform.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        LoginResponse response = authService.refreshTokens(request.refreshToken());

        return ResponseEntity.ok(response);
    }
}
