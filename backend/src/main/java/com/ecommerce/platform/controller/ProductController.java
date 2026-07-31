
package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.product.CreateProductRequest;
import com.ecommerce.platform.dto.request.product.UpdateProductRequest;
import com.ecommerce.platform.dto.response.ProductResponse;
import com.ecommerce.platform.validation.TenantRequestValidator;
import com.ecommerce.platform.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/{tenant}/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;

    private final TenantRequestValidator tenantRequestValidator;





    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> create(

            @PathVariable String tenant,


            @Valid
            @RequestPart("product")
            CreateProductRequest request,


            @RequestPart("image")
            MultipartFile image

    ) {


        tenantRequestValidator.validateUserTenant(tenant);



        return ResponseEntity.ok(

                productService.create(
                        request,
                        image
                )

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

