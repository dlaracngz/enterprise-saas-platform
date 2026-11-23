package com.saasplatform.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Ad boş olamaz")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Soyad boş olamaz")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "E-posta boş olamaz")
        @Email(message = "Geçerli bir e-posta giriniz")
        String email,

        @NotBlank(message = "Kullanıcı adı boş olamaz")
        @Size(min = 3, max = 100)
        String username,

        @NotBlank(message = "Şifre boş olamaz")
        @Size(min = 8, max = 128)
        String password
) {
}
