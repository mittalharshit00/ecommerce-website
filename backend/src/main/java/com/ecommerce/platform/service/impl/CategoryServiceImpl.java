package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.category.CreateCategoryRequest;
import com.ecommerce.platform.dto.request.category.UpdateCategoryRequest;
import com.ecommerce.platform.dto.response.CategoryResponse;
import com.ecommerce.platform.entity.Category;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.CategoryMapper;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

        private final CategoryRepository categoryRepository;

        private final CategoryMapper categoryMapper;

        private final CurrentUserService currentUserService;

        @Override
        public CategoryResponse create(
                        CreateCategoryRequest request) {

                Tenant tenant = currentUserService.getCurrentTenant();

                Category category = categoryMapper.toEntity(request);

                category.setTenant(tenant);

                category = categoryRepository.save(category);

                return categoryMapper.toResponse(category);
        }

        @Override
        @Transactional(readOnly = true)
        public CategoryResponse getById(
                        Long id) {

                return categoryMapper.toResponse(
                                getCategory(id));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<CategoryResponse> getAll(
                        Pageable pageable) {

                Tenant tenant = currentUserService.getCurrentTenant();

                return categoryRepository
                                .findByTenant(
                                                tenant,
                                                pageable)
                                .map(categoryMapper::toResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<CategoryResponse> getAllGlobal(
                        Pageable pageable) {

                return categoryRepository
                                .findAll(pageable)
                                .map(categoryMapper::toResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public CategoryResponse getByIdGlobal(
                        Long id) {

                Category category = categoryRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Category not found."));

                return categoryMapper.toResponse(category);
        }

        @Override
        public CategoryResponse update(
                        Long id,
                        UpdateCategoryRequest request) {

                Category category = getCategory(id);

                category.setName(
                                request.getName());

                category = categoryRepository.save(category);

                return categoryMapper.toResponse(category);
        }

        @Override
        public void delete(
                        Long id) {

                categoryRepository.delete(
                                getCategory(id));
        }

        private Category getCategory(
                        Long id) {

                Tenant tenant = currentUserService.getCurrentTenant();

                return categoryRepository
                                .findByIdAndTenant(
                                                id,
                                                tenant)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Category not found."));
        }
}