package com.saasplatform.plan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer maxUsers,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
