package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByDomain(String domain);

    Optional<Tenant> findByDomainIgnoreCase(String domain);

    boolean existsByDomain(String domain);

    boolean existsByDomainIgnoreCase(String domain);

    boolean existsByNameIgnoreCase(String name);
}