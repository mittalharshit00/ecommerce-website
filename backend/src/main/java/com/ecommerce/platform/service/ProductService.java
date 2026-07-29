package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.request.product.CreateProductRequest;
import com.ecommerce.platform.dto.request.product.UpdateProductRequest;
import com.ecommerce.platform.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(
            CreateProductRequest request
    );


    ProductResponse getById(
            Long id
    );


    Page<ProductResponse> getAll(
            Pageable pageable
    );


    Page<ProductResponse> getByCategory(
            Long categoryId,
            Pageable pageable
    );


    Page<ProductResponse> getAllGlobal(
            Pageable pageable
    );


    ProductResponse getByIdGlobal(
            Long id
    );


    Page<ProductResponse> getByCategoryGlobal(
            Long categoryId,
            Pageable pageable
    );


    ProductResponse update(
            Long id,
            UpdateProductRequest request
    );


    void delete(
            Long id
    );

}