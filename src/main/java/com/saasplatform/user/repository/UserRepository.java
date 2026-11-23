package com.saasplatform.user.repository;

import com.saasplatform.user.entity.User;
import com.saasplatform.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findAllByCompany_Id(UUID companyId);

    boolean existsByRole(UserRole role);

    Optional<User> findByIdAndCompany_Id(UUID userId, UUID companyId);
}
