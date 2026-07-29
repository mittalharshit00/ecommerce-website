package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import com.ecommerce.platform.validation.TenantRequestValidator;
import com.ecommerce.platform.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenant}/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;
    private final TenantRequestValidator tenantRequestValidator;

    @PostMapping("/{productId}")
    public ResponseEntity<FavouriteResponse> add(
            @PathVariable String tenant,
            @PathVariable Long productId
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                favouriteService.add(productId)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @PathVariable String tenant,
            @PathVariable Long productId
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        favouriteService.remove(productId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FavouriteResponse>> getMyFavourites(
            @PathVariable String tenant,
            Pageable pageable
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                favouriteService.getMyFavourites(pageable)
        );
    }
}