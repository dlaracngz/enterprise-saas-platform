package com.saasplatform.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    private Jwt getJwt(){
        // İsteği yapan kullanıcının kimlik doğrulama bilgilerini alır
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof  Jwt)){
            throw new IllegalStateException(("Kullanıcı kimliği doğrulanamadı"));
        }

        return (Jwt) authentication.getPrincipal();
    }

    public UUID getUserId(){
        return UUID.fromString(getJwt().getSubject());
    }

    public UUID getCompanyId(){
        String companyId = getJwt().getClaimAsString("companyId");
        return companyId == null ? null : UUID.fromString(companyId);
    }

    public String getRole(){
        return getJwt().getClaimAsString("role");
    }
}
