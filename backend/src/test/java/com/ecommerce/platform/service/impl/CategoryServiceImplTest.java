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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Tenant tenant;
    private Category category;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {

        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Local");

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setTenant(tenant);

        response = new CategoryResponse();
        response.setId(1L);
        response.setName("Electronics");
    }

    @Test
    void create_ShouldCreateCategory() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result = categoryService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getById_ShouldReturnCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result = categoryService.getById(1L);

        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found.");
    }

    @Test
    void getAll_ShouldReturnPage() {

        Page<Category> page =
                new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        Page<CategoryResponse> result =
                categoryService.getAll(PageRequest.of(0,10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName())
                .isEqualTo("Electronics");
    }

    @Test
    void getAllGlobal_ShouldReturnPage() {

        Page<Category> page =
                new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        Page<CategoryResponse> result =
                categoryService.getAllGlobal(PageRequest.of(0,10));

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getByIdGlobal_ShouldReturnCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result =
                categoryService.getByIdGlobal(1L);

        assertThat(result.getName())
                .isEqualTo("Electronics");
    }

    @Test
    void getByIdGlobal_ShouldThrowException() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.getByIdGlobal(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found.");
    }

    @Test
    void update_ShouldUpdateCategory() {

        UpdateCategoryRequest request =
                new UpdateCategoryRequest();

        request.setName("Mobiles");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result =
                categoryService.update(1L, request);

        verify(categoryRepository).save(category);

        assertThat(category.getName())
                .isEqualTo("Mobiles");

        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldThrowException_WhenCategoryNotFound() {

        UpdateCategoryRequest request =
                new UpdateCategoryRequest();

        request.setName("Mobiles");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.update(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found.");
    }

    @Test
    void delete_ShouldDeleteCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository)
                .delete(category);
    }

    @Test
    void delete_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found.");
    }

}