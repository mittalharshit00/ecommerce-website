package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import com.ecommerce.platform.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @PostMapping("/{productId}")
    public ResponseEntity<FavouriteResponse> add(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                favouriteService.add(productId)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long productId
    ) {

        favouriteService.remove(productId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FavouriteResponse>> getMyFavourites(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                favouriteService.getMyFavourites(pageable)
        );
    }
}