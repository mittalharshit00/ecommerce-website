package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Order;
import com.ecommerce.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUser(
            Long id,
            User user
    );

    Page<Order> findByUser(
            User user,
            Pageable pageable
    );

}