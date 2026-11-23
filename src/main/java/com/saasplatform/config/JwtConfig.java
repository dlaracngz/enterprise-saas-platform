package com.saasplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    // JWTConfig Spring Security'nin gelen token'ları doğrulayabilmesi için JWTDecoder nesnesini hazırlar
    public JwtDecoder jwtDecoder(
            @Value("${app.jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);

        SecretKey secretKey = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

}
