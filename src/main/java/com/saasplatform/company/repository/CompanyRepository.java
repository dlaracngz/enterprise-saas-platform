package com.saasplatform.company.repository;

import com.saasplatform.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,UUID>{
    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
