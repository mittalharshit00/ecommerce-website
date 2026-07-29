package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Category;
import com.ecommerce.platform.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndTenant(
            Long id,
            Tenant tenant
    );

    Page<Category> findByTenant(
            Tenant tenant,
            Pageable pageable
    );

}