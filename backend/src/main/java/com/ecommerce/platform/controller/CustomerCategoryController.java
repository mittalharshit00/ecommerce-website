package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.CategoryResponse;
import com.ecommerce.platform.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CustomerCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                categoryService.getAllGlobal(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                categoryService.getByIdGlobal(id)
        );
    }
}