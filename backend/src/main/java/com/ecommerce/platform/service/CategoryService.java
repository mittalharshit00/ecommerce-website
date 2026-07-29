package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.request.category.CreateCategoryRequest;
import com.ecommerce.platform.dto.request.category.UpdateCategoryRequest;
import com.ecommerce.platform.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponse create(
            CreateCategoryRequest request
    );


    CategoryResponse getById(
            Long id
    );


    Page<CategoryResponse> getAll(
            Pageable pageable
    );


    Page<CategoryResponse> getAllGlobal(
            Pageable pageable
    );


    CategoryResponse getByIdGlobal(
            Long id
    );


    CategoryResponse update(
            Long id,
            UpdateCategoryRequest request
    );


    void delete(
            Long id
    );

}