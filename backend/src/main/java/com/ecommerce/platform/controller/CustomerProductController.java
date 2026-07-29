package com.ecommerce.platform.controller;


import com.ecommerce.platform.dto.response.ProductResponse;
import com.ecommerce.platform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CustomerProductController {


    private final ProductService productService;



    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            Pageable pageable
    ){

        return ResponseEntity.ok(
                productService.getAllGlobal(pageable)
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(
                productService.getByIdGlobal(id)
        );
    }



    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getByCategory(
            @PathVariable Long categoryId,
            Pageable pageable
    ){

        return ResponseEntity.ok(
                productService.getByCategoryGlobal(
                        categoryId,
                        pageable
                )
        );
    }

}