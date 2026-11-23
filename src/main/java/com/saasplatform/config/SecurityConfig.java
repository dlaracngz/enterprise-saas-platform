
package com.saasplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // JWT içerisindeki rol bilgisini Spring Security yetkisine dönüştürür.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Hangi adreslerin kimlik doğrulaması gerekip gerekmediği
                .authorizeHttpRequests(auth -> auth

                        // Kimlik doğrulaması gerektirmeyenler
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh"
                        ).permitAll()

                        // Şirket kullanıcılarını yönetme adresleri
                        .requestMatchers("/api/companies/*/users/**")
                        .hasAnyRole("PLATFORM_ADMIN", "TENANT_ADMIN")

                        // Tenant Admin oluşturma yalnızca Platform Admin
                        .requestMatchers("/api/companies/*/tenant-admins")
                        .hasRole("PLATFORM_ADMIN")

                        // Tüm şirketleri listeleme
                        .requestMatchers(HttpMethod.GET, "/api/companies")
                        .hasRole("PLATFORM_ADMIN")

                        // Şirket oluşturma
                        .requestMatchers(HttpMethod.POST, "/api/companies")
                        .hasRole("PLATFORM_ADMIN")

                        // Şirket detayını görüntüleme
                        .requestMatchers(HttpMethod.GET, "/api/companies/*")
                        .hasAnyRole("PLATFORM_ADMIN", "TENANT_ADMIN")

                        // Şirket bilgilerini güncelleme
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*")
                        .hasAnyRole("PLATFORM_ADMIN", "TENANT_ADMIN")

                        // Şirketi aktifleştirme ve pasifleştirme
                        .requestMatchers(HttpMethod.PATCH, "/api/companies/**")
                        .hasRole("PLATFORM_ADMIN")

                        // Kullanıcı yönetimi
                        .requestMatchers("/api/users/**")
                        .hasAnyRole("PLATFORM_ADMIN", "TENANT_ADMIN")

                        // Diğer bütün adresler kimlik doğrulaması gerektirir
                        .anyRequest().authenticated()
                )

                // JWT doğrulamasını etkinleştirme
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }
}
