package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.product.CreateProductRequest;
import com.ecommerce.platform.dto.request.product.UpdateProductRequest;
import com.ecommerce.platform.dto.response.ProductResponse;
import com.ecommerce.platform.entity.Category;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.ProductMapper;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.impl.ProductServiceImpl;

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
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Tenant tenant;
    private Category category;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        tenant = Tenant.builder()
                .name("Local Tenant")
                .domain("local")
                .enabled(true)
                .build();

        category = Category.builder()
                .name("Electronics")
                .tenant(tenant)
                .build();

        product = Product.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .imageUrl("laptop.jpg")
                .price(new BigDecimal("1000.00"))
                .quantity(10)
                .tenant(tenant)
                .category(category)
                .build();

        productResponse = new ProductResponse();

        productResponse.setId(1L);
        productResponse.setName("Laptop");
        productResponse.setDescription("Gaming laptop");
        productResponse.setImageUrl("laptop.jpg");
        productResponse.setPrice(new BigDecimal("1000.00"));
        productResponse.setQuantity(10);
        productResponse.setCategoryId(1L);
        productResponse.setCategoryName("Electronics");
    }

    @Test
    void create_shouldCreateProductForCurrentTenant() {

        CreateProductRequest request =
                new CreateProductRequest();

        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setImageUrl("laptop.jpg");
        request.setPrice(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCategoryId(1L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(category));

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result =
                productService.create(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(
                new BigDecimal("1000.00"),
                result.getPrice()
        );
        assertEquals(10, result.getQuantity());

        assertSame(tenant, product.getTenant());
        assertSame(category, product.getCategory());

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findByIdAndTenant(1L, tenant);

        verify(productMapper)
                .toEntity(request);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void create_shouldThrowException_whenCategoryDoesNotExist() {

        CreateProductRequest request =
                new CreateProductRequest();

        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCategoryId(99L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.create(request)
        );

        verify(categoryRepository)
                .findByIdAndTenant(99L, tenant);

        verify(productMapper, never())
                .toEntity(any());

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void getById_shouldReturnProduct_whenProductBelongsToTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result =
                productService.getById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(
                new BigDecimal("1000.00"),
                result.getPrice()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByIdAndTenant(1L, tenant);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getById_shouldThrowException_whenProductDoesNotExist() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getById(99L)
        );

        verify(productRepository)
                .findByIdAndTenant(99L, tenant);

        verify(productMapper, never())
                .toResponse(any());
    }

    @Test
    void getAll_shouldReturnProductsForCurrentTenant() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByTenant(
                tenant,
                pageable
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        Page<ProductResponse> result =
                productService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        assertEquals(
                "Laptop",
                result.getContent()
                        .getFirst()
                        .getName()
        );

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByTenant(
                        tenant,
                        pageable
                );

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getByCategory_shouldReturnProductsForCategoryAndTenant() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByCategoryIdAndTenant(
                1L,
                tenant,
                pageable
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        Page<ProductResponse> result =
                productService.getByCategory(
                        1L,
                        pageable
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByCategoryIdAndTenant(
                        1L,
                        tenant,
                        pageable
                );

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void update_shouldUpdateProduct_withoutChangingCategory() {

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setName("Gaming Laptop");
        request.setDescription("Updated laptop");
        request.setImageUrl("updated.jpg");
        request.setPrice(new BigDecimal("1500.00"));
        request.setQuantity(20);
        request.setCategoryId(null);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result =
                productService.update(
                        1L,
                        request
                );

        assertNotNull(result);

        verify(productMapper)
                .updateEntity(
                        request,
                        product
                );

        verify(productRepository)
                .save(product);

        verify(categoryRepository, never())
                .findByIdAndTenant(
                        anyLong(),
                        any()
                );
    }

    @Test
    void update_shouldChangeCategory_whenCategoryIdProvided() {

        Category newCategory = Category.builder()
                .name("Computers")
                .tenant(tenant)
                .build();

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setName("Gaming Laptop");
        request.setDescription("Updated laptop");
        request.setImageUrl("updated.jpg");
        request.setPrice(new BigDecimal("1500.00"));
        request.setQuantity(20);
        request.setCategoryId(2L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(categoryRepository.findByIdAndTenant(
                2L,
                tenant
        )).thenReturn(Optional.of(newCategory));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        ProductResponse result =
                productService.update(
                        1L,
                        request
                );

        assertNotNull(result);

        assertSame(
                newCategory,
                product.getCategory()
        );

        verify(categoryRepository)
                .findByIdAndTenant(
                        2L,
                        tenant
                );

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .updateEntity(
                        request,
                        product
                );
    }

    @Test
    void update_shouldThrowException_whenNewCategoryDoesNotExist() {

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setName("Gaming Laptop");
        request.setDescription("Updated laptop");
        request.setPrice(new BigDecimal("1500.00"));
        request.setQuantity(20);
        request.setCategoryId(99L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(categoryRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(
                        1L,
                        request
                )
        );

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void delete_shouldDeleteProduct_whenProductBelongsToTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(productRepository)
                .delete(product);
    }

    @Test
    void delete_shouldThrowException_whenProductDoesNotExist() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                99L,
                tenant
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(99L)
        );

        verify(productRepository, never())
                .delete(any());
    }
}