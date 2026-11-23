package com.saasplatform.bootstrap;

import com.saasplatform.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "app.bootstrap-admin.enabled",
        havingValue = "true"
)
public class PlatformAdminInitializer implements ApplicationRunner {

    private final UserService userService;

    @Value("${app.bootstrap-admin.first-name}")
    private String firstName;

    @Value("${app.bootstrap-admin.last-name}")
    private String lastName;

    @Value("${app.bootstrap-admin.email}")
    private String email;

    @Value("${app.bootstrap-admin.username}")
    private String username;

    @Value("${app.bootstrap-admin.password}")
    private String password;

    public PlatformAdminInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Platform Admin şifresi tanımlanmamıştır."
            );
        }
        userService.createPlatformAdmin(
                firstName,
                lastName,
                email,
                username,
                password
        );
    }
}
