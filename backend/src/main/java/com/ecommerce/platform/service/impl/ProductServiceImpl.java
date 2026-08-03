
package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.product.CreateProductRequest;
import com.ecommerce.platform.dto.request.product.UpdateProductRequest;
import com.ecommerce.platform.dto.response.ProductResponse;
import com.ecommerce.platform.entity.Category;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.ProductMapper;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.FileStorageService;
import com.ecommerce.platform.service.ProductService;

import lombok.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {


        private final ProductRepository productRepository;

        private final CategoryRepository categoryRepository;

        private final ProductMapper productMapper;

        private final CurrentUserService currentUserService;

        private final FileStorageService fileStorageService;





        @Override
        public ProductResponse create(
                        CreateProductRequest request,
                        MultipartFile image) {



                Tenant tenant =
                        currentUserService.getCurrentTenant();




                Category category =
                        categoryRepository
                                .findById(
                                        request.getCategoryId()
                                )
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Category not found."
                                        )
                                );





                String imageUrl =
                        fileStorageService
                                .storeProductImage(image);





                Product product =
                        productMapper.toEntity(request);



                product.setTenant(tenant);

                product.setCategory(category);

                product.setImageUrl(imageUrl);




                product =
                        productRepository.save(product);




                return productMapper.toResponse(product);

        }









        private Product getProduct(Long id) {


                Tenant tenant =
                        currentUserService.getCurrentTenant();



                return productRepository
                        .findByIdAndTenant(
                                id,
                                tenant
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found."
                                )
                        );

        }









        @Override
        @Transactional(readOnly = true)
        public ProductResponse getById(
                        Long id) {


                return productMapper.toResponse(
                        getProduct(id)
                );

        }









        @Override
        @Transactional(readOnly = true)
        public Page<ProductResponse> getAll(
                        Pageable pageable) {


                Tenant tenant =
                        currentUserService.getCurrentTenant();



                return productRepository
                        .findByTenant(
                                tenant,
                                pageable
                        )
                        .map(productMapper::toResponse);

        }









        @Override
        @Transactional(readOnly = true)
        public Page<ProductResponse> getByCategory(
                        Long categoryId,
                        Pageable pageable) {


                return productRepository
                        .findByCategoryId(
                                categoryId,
                                pageable
                        )
                        .map(productMapper::toResponse);

        }









        @Override
        @Transactional(readOnly = true)
        public Page<ProductResponse> getAllGlobal(
                        Pageable pageable) {


                return productRepository
                        .findAll(pageable)
                        .map(productMapper::toResponse);

        }









        @Override
        @Transactional(readOnly = true)
        public ProductResponse getByIdGlobal(
                        Long id) {


                Product product =
                        productRepository
                                .findById(id)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Product not found."
                                        )
                                );



                return productMapper.toResponse(product);

        }









        @Override
        @Transactional(readOnly = true)
        public Page<ProductResponse> getByCategoryGlobal(
                        Long categoryId,
                        Pageable pageable) {


                return productRepository
                        .findByCategoryId(
                                categoryId,
                                pageable
                        )
                        .map(productMapper::toResponse);

        }









        @Override
        public ProductResponse update(
                        Long id,
                        UpdateProductRequest request) {


                Product product =
                        getProduct(id);



                productMapper.updateEntity(
                        request,
                        product
                );



                if (request.getCategoryId() != null) {


                        Category category =
                                categoryRepository
                                        .findById(
                                                request.getCategoryId()
                                        )
                                        .orElseThrow(() ->
                                                new ResourceNotFoundException(
                                                        "Category not found."
                                                )
                                        );


                        product.setCategory(category);

                }




                product =
                        productRepository.save(product);



                return productMapper.toResponse(product);

        }









        @Override
        public void delete(
                        Long id) {


                productRepository.delete(
                        getProduct(id)
                );

        }
}

