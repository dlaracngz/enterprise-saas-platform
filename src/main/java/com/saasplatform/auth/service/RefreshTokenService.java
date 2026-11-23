package com.saasplatform.auth.service;

import com.saasplatform.auth.entity.RefreshToken;
import com.saasplatform.auth.repository.RefreshTokenRepository;
import com.saasplatform.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Random token üretmek
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    // Token hashlemek
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Token hash oluşturulamadı.", exception);
        }
    }

    // Hashlenmiş token'ı veritabanına kaydetmek
    public String createRefreshToken(User user) {

        String token = generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(token));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusSeconds(refreshTokenExpiration)
        );

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    // Refresh Token'ın geçerli olup olmadığını kontrol etmek
    public Optional<RefreshToken> findValidRefreshToken(String token) {

        String tokenHash = hashToken(token);

        return refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(refreshToken -> refreshToken.getRevokedAt() == null)
                .filter(refreshToken -> refreshToken.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    // Refresh Token iptal etme -> Özellikle çıkış yapıldığında veya token yenilendiğinde kullanılacak
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = findValidRefreshToken(token)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Refresh token geçersiz veya süresi dolmuş"
                ));
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    // Eski refresh token'ı pasifleştirip yerine yenisini eklemek
    @Transactional
    public String rotateRefreshToken(RefreshToken oldToken) {
        User user = oldToken.getUser();

        // Eski token'ı geçersiz hale getir
        oldToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldToken);

        // Yeni token oluştur
        return createRefreshToken(user);
    }
}
