package com.saasplatform.user.dto;

import com.saasplatform.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID companyId,
        String firstName,
        String lastName,
        String email,
        String username,
        UserRole role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}