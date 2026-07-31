package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByIdAndTenant(
            Long id,
            Tenant tenant
    );

    Optional<User> findByTenantAndUsername(
            Tenant tenant,
            String username
    );

    Optional<User> findByTenantAndEmail(
            Tenant tenant,
            String email
    );

    Page<User> findByTenant(
            Tenant tenant,
            Pageable pageable
    );

    Optional<User> findByUsername(String username);

        Optional<User> findByEmail(String email);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

    boolean existsByTenantAndUsername(
            Tenant tenant,
            String username
    );

    boolean existsByTenantAndEmail(
            Tenant tenant,
            String email
    );

    List<User> findByTenantIsNull();

}