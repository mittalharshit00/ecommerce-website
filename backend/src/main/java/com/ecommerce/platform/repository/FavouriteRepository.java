package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Favourite;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavouriteRepository
        extends JpaRepository<Favourite, Long> {

    Optional<Favourite> findByUserAndProduct(
            User user,
            Product product
    );

    Page<Favourite> findByUser(
            User user,
            Pageable pageable
    );

}