package com.saasplatform.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

// API'den döndürülecek şirket verileri
public record CompanyResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String taxNumber,
        String address,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
