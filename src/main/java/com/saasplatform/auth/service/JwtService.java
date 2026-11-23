package com.saasplatform.auth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.saasplatform.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        // Base64 metni çözerek Byte dizisine dönüştürür. İmzalama işleminde kullanılabilecek veriyi hazırlama
        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);

        // SecretKeySpec -> Byte dizisini kriptografik anahtar nesnesine dönüştürme
        // HmacSHA256 -> Kullanacağımız imzalama algoritması
        SecretKey secretKey = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        // Bu satırın amacı, JWT üretecek mekanizmayı oluşturmak ve ona gizli anahtarımızı tanıtmak
        // NimbusJwtEncoder -> JWT oluşturmak ve imzalamak için kullandığımız sınıf
        // ImmutableSecret -> Oluşturulan gizli anahtarı JWT Encoder'a sunar.
        this.jwtEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );

        this.accessTokenExpiration = accessTokenExpiration;
    }

    // Access Token oluşturma ve İmzalama
    public String generateAccessToken(User user){
        // Token oluşturulma anı almak için
        Instant now = Instant.now();

        // JWT içerisine bilgileri ekleme
        // issuer -> Token'ı hangi sistemin oluşturduğunu belirtir.
        // issuedAt -> Token'ın oluşturulma zamanını belirtir
        // expiresAt -> Token'ın sona ereceği zamanı belirtir
        // subject -> Token'ın hangi kullanıcıya ait olduğunu belirtir
        // claim -> Token'a özel bilgi ekler
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("saas-platform")
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenExpiration))
                .subject(user.getId().toString())
                .claim("role",user.getRole().name());

        // Kullanıcının bir şirkete bağlı olup olmadığını kontrol eder
        // Varsa şirket ID'sini JWT içerisine ekler, yoksa companyId eklemez
        if(user.getCompany() != null){
            claimsBuilder.claim(
                    "companyId",
                    user.getCompany().getId().toString()
            );
        }

        // Token içerisinde bulunacak kullanıcı bilgileri hazırlama
        JwtClaimsSet claims = claimsBuilder.build();

        // JWT'nin hangi algoritmayla imzalanacağını belirtir
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // JWT üretmek için gerekenleri birleştirme, Encoder'a göndereceğimiz bilgiler hazır
        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(header,claims);

        // Jwt oluşturma ve imzalama
        return jwtEncoder.encode(parameters).getTokenValue();
    }
}
