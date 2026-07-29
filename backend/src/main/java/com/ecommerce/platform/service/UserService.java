package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse syncCurrentUser();

    UserResponse getCurrentUser();

    UserResponse getById(Long id);

    Page<UserResponse> getAll(Pageable pageable);
    

}