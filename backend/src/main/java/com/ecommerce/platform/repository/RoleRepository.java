package com.ecommerce.platform.repository;

import com.ecommerce.platform.entity.Role;
import com.ecommerce.platform.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

}