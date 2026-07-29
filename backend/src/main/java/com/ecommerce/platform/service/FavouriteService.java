package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavouriteService {

    FavouriteResponse add(Long productId);

    void remove(Long productId);

    Page<FavouriteResponse> getMyFavourites(Pageable pageable);

}