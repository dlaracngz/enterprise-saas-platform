package com.saasplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration : Spring'e bir sınıfın uygulama yapılandırma sınıfı olduğunu söyler.
// Yani Spring'e bu sınıfın içerisinde uygulamanın nasıl çalışacağına ilişkin ayarlar tanımlayacağız.
@Configuration
public class PasswordConfig {
    // @Bean : Spring'e bir nesneyi kendisinin oluşturup yönetmesini söyler.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
