package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import com.ecommerce.platform.entity.Favourite;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.exception.ConflictException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.FavouriteMapper;
import com.ecommerce.platform.repository.FavouriteRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.impl.FavouriteServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private Tenant tenant;
    private User user;
    private Product product;
    private Favourite favourite;
    private FavouriteResponse favouriteResponse;

    @BeforeEach
    void setUp() {

        tenant = Tenant.builder()
                .name("Local Tenant")
                .domain("local")
                .enabled(true)
                .build();

        user = User.builder()
                .username("john")
                .email("john@example.com")
                .keycloakUserId("keycloak-user-1")
                .enabled(true)
                .tenant(tenant)
                .build();

        product = Product.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .imageUrl("laptop.jpg")
                .price(new BigDecimal("1000.00"))
                .quantity(10)
                .tenant(tenant)
                .build();

        favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);

        favouriteResponse = new FavouriteResponse();
    }

    @Test
    void add_shouldCreateFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(
                user,
                product
        )).thenReturn(Optional.empty());

        when(favouriteRepository.save(any(Favourite.class)))
                .thenReturn(favourite);

        when(favouriteMapper.toResponse(favourite))
                .thenReturn(favouriteResponse);

        FavouriteResponse result =
                favouriteService.add(1L);

        assertNotNull(result);

        verify(currentUserService)
                .getCurrentUser();

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(favouriteRepository)
                .findByUserAndProduct(
                        user,
                        product
                );

        verify(favouriteRepository)
                .save(any(Favourite.class));

        verify(favouriteMapper)
                .toResponse(favourite);
    }

    @Test
    void add_shouldThrowException_whenProductDoesNotExist() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.add(99L)
        );

        verify(productRepository)
                .findByIdAndTenant(
                        99L,
                        tenant
                );

        verify(favouriteRepository, never())
                .findByUserAndProduct(any(), any());

        verify(favouriteRepository, never())
                .save(any());
    }

    @Test
    void add_shouldThrowConflict_whenProductAlreadyFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(
                user,
                product
        )).thenReturn(Optional.of(favourite));

        assertThrows(
                ConflictException.class,
                () -> favouriteService.add(1L)
        );

        verify(favouriteRepository)
                .findByUserAndProduct(
                        user,
                        product
                );

        verify(favouriteRepository, never())
                .save(any());

        verify(favouriteMapper, never())
                .toResponse(any());
    }

    @Test
    void remove_shouldDeleteFavourite() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(
                user,
                product
        )).thenReturn(Optional.of(favourite));

        favouriteService.remove(1L);

        verify(currentUserService)
                .getCurrentUser();

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(favouriteRepository)
                .findByUserAndProduct(
                        user,
                        product
                );

        verify(favouriteRepository)
                .delete(favourite);
    }

    @Test
    void remove_shouldThrowException_whenProductDoesNotExist() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.remove(99L)
        );

        verify(favouriteRepository, never())
                .findByUserAndProduct(any(), any());

        verify(favouriteRepository, never())
                .delete(any());
    }

    @Test
    void remove_shouldThrowException_whenFavouriteDoesNotExist() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(favouriteRepository.findByUserAndProduct(
                user,
                product
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> favouriteService.remove(1L)
        );

        verify(favouriteRepository)
                .findByUserAndProduct(
                        user,
                        product
                );

        verify(favouriteRepository, never())
                .delete(any());
    }

    @Test
    void getMyFavourites_shouldReturnUserFavourites() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Favourite> favouritePage =
                new PageImpl<>(
                        List.of(favourite)
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(favouriteRepository.findByUser(
                user,
                pageable
        )).thenReturn(favouritePage);

        when(favouriteMapper.toResponse(favourite))
                .thenReturn(favouriteResponse);

        Page<FavouriteResponse> result =
                favouriteService.getMyFavourites(
                        pageable
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        verify(currentUserService)
                .getCurrentUser();

        verify(favouriteRepository)
                .findByUser(
                        user,
                        pageable
                );

        verify(favouriteMapper)
                .toResponse(favourite);
    }

    @Test
    void getMyFavourites_shouldReturnEmptyPage_whenUserHasNoFavourites() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Favourite> emptyPage =
                new PageImpl<>(List.of());

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(favouriteRepository.findByUser(
                user,
                pageable
        )).thenReturn(emptyPage);

        Page<FavouriteResponse> result =
                favouriteService.getMyFavourites(
                        pageable
                );

        assertNotNull(result);

        assertTrue(result.isEmpty());

        assertEquals(
                0,
                result.getTotalElements()
        );

        verify(currentUserService)
                .getCurrentUser();

        verify(favouriteRepository)
                .findByUser(
                        user,
                        pageable
                );
    }
}