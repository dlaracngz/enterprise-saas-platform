package com.saasplatform.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyUpdateRequest(
        @NotBlank(message = "Şirket adı boş olamaz")
        @Size(max = 150)
        String name,

        @NotBlank(message = "E-posta boş olamaz")
        @Email(message = "Geçerli bir e-posta giriniz")
        String email,

        String phone,
        String taxNumber,
        String address
) {
}
