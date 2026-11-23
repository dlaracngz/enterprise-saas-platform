package com.saasplatform.auth.repository;

import com.saasplatform.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    // Token hash değerine göre kayıt bulur
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Bir kullanıcının bütün refresh token kayıtlarını siler
    void deleteAllByUser_Id(UUID userId);
}
