package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {



    Optional<Product> findByIdAndTenant(
            Long id,
            Tenant tenant
    );


    Page<Product> findByTenant(
            Tenant tenant,
            Pageable pageable
    );


    Page<Product> findByCategoryIdAndTenant(
            Long categoryId,
            Tenant tenant,
            Pageable pageable
    );




    Page<Product> findAll(
            Pageable pageable
    );


    Optional<Product> findById(
            Long id
    );


    Page<Product> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

}