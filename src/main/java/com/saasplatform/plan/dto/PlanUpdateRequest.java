package com.saasplatform.plan.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlanUpdateRequest(

        @NotBlank(message = "Plan adı boş olamaz")
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull(message = "Plan fiyatı zorunludur.")
        @DecimalMin(value = "0.0", message = "Fiyat negatif olamaz.")
        BigDecimal price,

        @NotNull(message = "Maksimum kullanıcı sayısı zorunludur.")
        @Min(value = 1, message = "Kullanıcı sayısı en az 1 olmalıdır.")
        Integer maxUsers
) {
}
