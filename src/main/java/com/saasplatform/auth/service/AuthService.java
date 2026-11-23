package com.saasplatform.auth.service;

import com.saasplatform.auth.dto.LoginRequest;
import com.saasplatform.auth.dto.LoginResponse;
import com.saasplatform.auth.entity.RefreshToken;
import com.saasplatform.user.entity.User;
import com.saasplatform.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException(
                        "E-posta veya şifre hatalı"
                ));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException(
                    "E-posta veya şifre hatalı"
            );
        }
        if (!user.isActive()) {
            throw new BadCredentialsException(
                    "Hesap aktif değil"
            );
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900
        );
    }

    @Transactional
    public LoginResponse refreshTokens(String token) {

        RefreshToken oldToken = refreshTokenService
                .findValidRefreshToken(token)
                .orElseThrow(() -> new BadCredentialsException(
                        "Refresh token geçersiz veya süresi dolmuş"
                ));

        User user = oldToken.getUser();

        if (!user.isActive()) {
            throw new BadCredentialsException("Hesap aktif değil");
        }

        String newRefreshToken =
                refreshTokenService.rotateRefreshToken(oldToken);

        String newAccessToken =
                jwtService.generateAccessToken(user);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                900
        );
    }
}
