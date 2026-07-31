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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FavouriteMapper favouriteMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private User user;
    private Product product;
    private Favourite favourite;
    private FavouriteResponse response;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("john");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        favourite = new Favourite();
        favourite.setId(1L);
        favourite.setUser(user);
        favourite.setProduct(product);

        response = new FavouriteResponse();
        response.setId(1L);
    }

    @Test
    void add_ShouldAddFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        when(favouriteRepository.save(any(Favourite.class)))
                .thenReturn(favourite);

        when(favouriteMapper.toResponse(favourite))
                .thenReturn(response);

        FavouriteResponse result = favouriteService.add(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(favouriteRepository).save(any(Favourite.class));
    }

    @Test
    void add_ShouldThrow_WhenProductNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favouriteService.add(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found.");

        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void add_ShouldThrow_WhenAlreadyFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(favourite));

        assertThatThrownBy(() -> favouriteService.add(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Product already added to favourites.");

        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void remove_ShouldDeleteFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.of(favourite));

        favouriteService.remove(1L);

        verify(favouriteRepository).delete(favourite);
    }

    @Test
    void remove_ShouldThrow_WhenProductNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favouriteService.remove(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found.");

        verify(favouriteRepository, never()).delete(any());
    }

    @Test
    void remove_ShouldThrow_WhenFavouriteNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(user, product))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favouriteService.remove(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Favourite not found.");

        verify(favouriteRepository, never()).delete(any());
    }

    @Test
    void getMyFavourites_ShouldReturnPage() {

        Page<Favourite> page = new PageImpl<>(List.of(favourite));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(favouriteRepository.findByUser(eq(user), any(PageRequest.class)))
                .thenReturn(page);

        when(favouriteMapper.toResponse(favourite))
                .thenReturn(response);

        Page<FavouriteResponse> result =
                favouriteService.getMyFavourites(PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        verify(favouriteRepository)
                .findByUser(eq(user), any(PageRequest.class));
    }

    @Test
    void getMyFavourites_ShouldReturnEmptyPage() {

        Page<Favourite> page = Page.empty();

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(favouriteRepository.findByUser(eq(user), any(PageRequest.class)))
                .thenReturn(page);

        Page<FavouriteResponse> result =
                favouriteService.getMyFavourites(PageRequest.of(0, 10));

        assertThat(result).isEmpty();
    }

}