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
import com.ecommerce.platform.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {

        tenant = Tenant.builder()
                .name("Local Tenant")
                .domain("local")
                .enabled(true)
                .build();

        category = Category.builder()
                .name("Electronics")
                .tenant(tenant)
                .build();

        categoryResponse = new CategoryResponse();

        categoryResponse.setName("Electronics");
    }

    @Test
    void create_shouldCreateCategoryForCurrentTenant() {

        CreateCategoryRequest request =
                new CreateCategoryRequest();

        request.setName("Electronics");

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        CategoryResponse result =
                categoryService.create(request);

        assertNotNull(result);
        assertEquals(
                "Electronics",
                result.getName()
        );

        assertSame(
                tenant,
                category.getTenant()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryMapper)
                .toEntity(request);

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void getById_shouldReturnCategory_whenCategoryBelongsToTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        CategoryResponse result =
                categoryService.getById(1L);

        assertNotNull(result);
        assertEquals(
                "Electronics",
                result.getName()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void getById_shouldThrowException_whenCategoryDoesNotExist() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getById(99L)
        );

        verify(categoryRepository)
                .findByIdAndTenant(
                        99L,
                        tenant
                );

        verify(categoryMapper, never())
                .toResponse(any());
    }

    @Test
    void getAll_shouldReturnCategoriesForCurrentTenant() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Category> categoryPage =
                new PageImpl<>(
                        List.of(category)
                );

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByTenant(
                tenant,
                pageable
        )).thenReturn(categoryPage);

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        Page<CategoryResponse> result =
                categoryService.getAll(pageable);

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                "Electronics",
                result.getContent()
                        .getFirst()
                        .getName()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findByTenant(
                        tenant,
                        pageable
                );

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void update_shouldUpdateCategoryName() {

        UpdateCategoryRequest request =
                new UpdateCategoryRequest();

        request.setName("Computers");

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(categoryResponse);

        CategoryResponse result =
                categoryService.update(
                        1L,
                        request
                );

        assertNotNull(result);

        assertEquals(
                "Computers",
                category.getName()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void update_shouldThrowException_whenCategoryDoesNotExist() {

        UpdateCategoryRequest request =
                new UpdateCategoryRequest();

        request.setName("Computers");

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.update(
                        99L,
                        request
                )
        );

        verify(categoryRepository)
                .findByIdAndTenant(
                        99L,
                        tenant
                );

        verify(categoryRepository, never())
                .save(any());

        verify(categoryMapper, never())
                .toResponse(any());
    }

    @Test
    void delete_shouldDeleteCategory_whenCategoryBelongsToTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(categoryRepository)
                .delete(category);
    }

    @Test
    void delete_shouldThrowException_whenCategoryDoesNotExist() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.delete(99L)
        );

        verify(categoryRepository, never())
                .delete(any());
    }
}