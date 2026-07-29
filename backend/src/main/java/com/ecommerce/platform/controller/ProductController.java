package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.product.CreateProductRequest;
import com.ecommerce.platform.dto.request.product.UpdateProductRequest;
import com.ecommerce.platform.dto.response.ProductResponse;
import com.ecommerce.platform.validation.*;
import com.ecommerce.platform.service.ProductService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenant}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final TenantRequestValidator tenantRequestValidator;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @PathVariable String tenant,

            @Valid
            @RequestBody
            CreateProductRequest request
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                productService.create(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            @PathVariable String tenant,
            Pageable pageable
    ) {

        tenantRequestValidator.validateTenantExists(tenant);

        return ResponseEntity.ok(
                productService.getAll(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable String tenant,
            @PathVariable Long id
    ) {

        tenantRequestValidator.validateTenantExists(tenant);

        return ResponseEntity.ok(
                productService.getById(id)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getByCategory(
            @PathVariable String tenant,
            @PathVariable Long categoryId,
            Pageable pageable
    ) {

        tenantRequestValidator.validateTenantExists(tenant);

        return ResponseEntity.ok(
                productService.getByCategory(
                        categoryId,
                        pageable
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable String tenant,
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateProductRequest request
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                productService.update(
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

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}