package com.saasplatform.plan.repository;

import com.saasplatform.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    Optional<Plan> findByName(String name);

    boolean existsByName(String name);
}
