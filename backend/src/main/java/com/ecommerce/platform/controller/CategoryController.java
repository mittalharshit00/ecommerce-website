package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.category.CreateCategoryRequest;
import com.ecommerce.platform.dto.request.category.UpdateCategoryRequest;
import com.ecommerce.platform.dto.response.CategoryResponse;
import com.ecommerce.platform.validation.TenantRequestValidator;
import com.ecommerce.platform.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenant}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final TenantRequestValidator tenantRequestValidator;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @PathVariable String tenant,

            @Valid
            @RequestBody
            CreateCategoryRequest request
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                categoryService.create(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(
            @PathVariable String tenant,
            Pageable pageable
    ) {

        tenantRequestValidator.validateTenantExists(tenant);

        return ResponseEntity.ok(
                categoryService.getAll(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable String tenant,
            @PathVariable Long id
    ) {

        tenantRequestValidator.validateTenantExists(tenant);

        return ResponseEntity.ok(
                categoryService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable String tenant,
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateCategoryRequest request
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                categoryService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String tenant,
            @PathVariable Long id
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}