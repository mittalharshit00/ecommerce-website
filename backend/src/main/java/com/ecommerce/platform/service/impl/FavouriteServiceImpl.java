package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import com.ecommerce.platform.entity.Favourite;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.exception.ConflictException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.FavouriteMapper;
import com.ecommerce.platform.repository.FavouriteRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepository favouriteRepository;

    private final ProductRepository productRepository;

    private final FavouriteMapper favouriteMapper;

    private final CurrentUserService currentUserService;

    @Override
    public FavouriteResponse add(Long productId) {

        User user = currentUserService.getCurrentUser();

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        favouriteRepository
                .findByUserAndProduct(user, product)
                .ifPresent(favourite -> {
                    throw new ConflictException(
                            "Product already added to favourites."
                    );
                });

        Favourite favourite = new Favourite();

        favourite.setUser(user);
        favourite.setProduct(product);

        favourite = favouriteRepository.save(favourite);

        return favouriteMapper.toResponse(favourite);
    }

    @Override
    public void remove(Long productId) {

        User user = currentUserService.getCurrentUser();

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        Favourite favourite = favouriteRepository
                .findByUserAndProduct(user, product)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Favourite not found."
                        )
                );

        favouriteRepository.delete(favourite);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavouriteResponse> getMyFavourites(
            Pageable pageable
    ) {

        return favouriteRepository
                .findByUser(
                        currentUserService.getCurrentUser(),
                        pageable
                )
                .map(favouriteMapper::toResponse);
    }
}