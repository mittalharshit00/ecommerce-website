
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
import com.ecommerce.platform.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Tenant tenant;
    private Category category;
    private Category secondCategory;
    private Product product;
    private ProductResponse productResponse;
    private MultipartFile image;

    @BeforeEach
    void setUp() {

        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Local");

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setTenant(tenant);

        secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Computers");
        secondCategory.setTenant(tenant);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setQuantity(10);
        product.setTenant(tenant);
        product.setCategory(category);
        product.setImageUrl("/uploads/products/laptop.jpg");

        productResponse = new ProductResponse();
        productResponse.setId(1L);

        image = new MockMultipartFile(
                "image",
                "laptop.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void create_ShouldCreateProductSuccessfully() {

        // Arrange

        CreateProductRequest request =
                new CreateProductRequest();

        request.setCategoryId(1L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(fileStorageService.storeProductImage(image))
                .thenReturn(
                        "/uploads/products/laptop.jpg"
                );

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        ProductResponse result =
                productService.create(
                        request,
                        image
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(currentUserService)
                .getCurrentTenant();

        verify(categoryRepository)
                .findById(1L);

        verify(fileStorageService)
                .storeProductImage(image);

        verify(productMapper)
                .toEntity(request);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void create_ShouldSetTenantCategoryAndImageUrl() {

        // Arrange

        CreateProductRequest request =
                new CreateProductRequest();

        request.setCategoryId(1L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(fileStorageService.storeProductImage(image))
                .thenReturn(
                        "/uploads/products/laptop.jpg"
                );

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse);

        // Act

        productService.create(
                request,
                image
        );

        // Assert

        ArgumentCaptor<Product> captor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository)
                .save(captor.capture());

        Product savedProduct =
                captor.getValue();

        assertThat(savedProduct.getTenant())
                .isEqualTo(tenant);

        assertThat(savedProduct.getCategory())
                .isEqualTo(category);

        assertThat(savedProduct.getImageUrl())
                .isEqualTo(
                        "/uploads/products/laptop.jpg"
                );
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenCategoryDoesNotExist() {

        // Arrange

        CreateProductRequest request =
                new CreateProductRequest();

        request.setCategoryId(999L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.create(
                        request,
                        image
                ))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Category not found.");

        verify(fileStorageService, never())
                .storeProductImage(any());

        verify(productMapper, never())
                .toEntity(any());

        verify(productRepository, never())
                .save(any());
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getById_ShouldReturnProductSuccessfully() {

        // Arrange

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        ProductResponse result =
                productService.getById(1L);

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(currentUserService)
                .getCurrentTenant();

        verify(productRepository)
                .findByIdAndTenant(
                        1L,
                        tenant
                );

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {

        // Arrange

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.getById(1L))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Product not found.");

        verify(productMapper, never())
                .toResponse(any(Product.class));
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAll_ShouldReturnTenantProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByTenant(
                eq(tenant),
                eq(pageable)
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        Page<ProductResponse> result =
                productService.getAll(pageable);

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .isEqualTo(productResponse);

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
    void getAll_ShouldReturnEmptyPage_WhenTenantHasNoProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> emptyPage =
                Page.empty(pageable);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByTenant(
                eq(tenant),
                eq(pageable)
        )).thenReturn(emptyPage);

        // Act

        Page<ProductResponse> result =
                productService.getAll(pageable);

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isZero();

        assertThat(result.getContent())
                .isEmpty();

        verify(productMapper, never())
                .toResponse(any(Product.class));
    }

    // =========================================================
    // GET BY CATEGORY
    // =========================================================

    @Test
    void getByCategory_ShouldReturnTenantCategoryProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(productRepository.findByCategoryId(
                1L,
                pageable
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        Page<ProductResponse> result =
                productService.getByCategory(
                        1L,
                        pageable
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        verify(productRepository)
                .findByCategoryId(
                        1L,
                        pageable
                );

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getByCategory_ShouldReturnEmptyPage_WhenCategoryHasNoProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> emptyPage =
                Page.empty(pageable);

        when(productRepository.findByCategoryId(
                1L,
                pageable
        )).thenReturn(emptyPage);

        // Act

        Page<ProductResponse> result =
                productService.getByCategory(
                        1L,
                        pageable
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getContent())
                .isEmpty();

        verify(productMapper, never())
                .toResponse(any(Product.class));
    }

    // =========================================================
    // GET ALL GLOBAL
    // =========================================================

    @Test
    void getAllGlobal_ShouldReturnAllProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        Page<ProductResponse> result =
                productService.getAllGlobal(pageable);

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        verify(productRepository)
                .findAll(pageable);

        verify(productMapper)
                .toResponse(product);
    }

    // =========================================================
    // GET BY ID GLOBAL
    // =========================================================

    @Test
    void getByIdGlobal_ShouldReturnProductSuccessfully() {

        // Arrange

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        ProductResponse result =
                productService.getByIdGlobal(1L);

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(productRepository)
                .findById(1L);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getByIdGlobal_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {

        // Arrange

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.getByIdGlobal(999L))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Product not found.");

        verify(productMapper, never())
                .toResponse(any(Product.class));
    }

    // =========================================================
    // GET BY CATEGORY GLOBAL
    // =========================================================

    @Test
    void getByCategoryGlobal_ShouldReturnProducts() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1
                );

        when(productRepository.findByCategoryId(
                1L,
                pageable
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        Page<ProductResponse> result =
                productService.getByCategoryGlobal(
                        1L,
                        pageable
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        verify(productRepository)
                .findByCategoryId(
                        1L,
                        pageable
                );

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void getByCategoryGlobal_ShouldReturnEmptyPage_WhenNoProductsExist() {

        // Arrange

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Product> emptyPage =
                Page.empty(pageable);

        when(productRepository.findByCategoryId(
                1L,
                pageable
        )).thenReturn(emptyPage);

        // Act

        Page<ProductResponse> result =
                productService.getByCategoryGlobal(
                        1L,
                        pageable
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(result.getContent())
                .isEmpty();

        verify(productMapper, never())
                .toResponse(any(Product.class));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void update_ShouldUpdateProductWithoutChangingCategory() {

        // Arrange

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setName("Updated Laptop");

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

        // Act

        ProductResponse result =
                productService.update(
                        1L,
                        request
                );

        // Assert

        assertThat(result)
                .isNotNull();

        verify(productMapper)
                .updateEntity(
                        request,
                        product
                );

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);

        verify(categoryRepository, never())
                .findById(any());
    }

    @Test
    void update_ShouldUpdateProductAndCategory() {

        // Arrange

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setCategoryId(2L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(secondCategory));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        // Act

        ProductResponse result =
                productService.update(
                        1L,
                        request
                );

        // Assert

        assertThat(result)
                .isNotNull();

        assertThat(product.getCategory())
                .isEqualTo(secondCategory);

        verify(productMapper)
                .updateEntity(
                        request,
                        product
                );

        verify(categoryRepository)
                .findById(2L);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(product);
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {

        // Arrange

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setName("Updated Laptop");

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                999L,
                tenant
        )).thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.update(
                        999L,
                        request
                ))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Product not found.");

        verify(productMapper, never())
                .updateEntity(
                        any(UpdateProductRequest.class),
                        any(Product.class)
                );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenNewCategoryDoesNotExist() {

        // Arrange

        UpdateProductRequest request =
                new UpdateProductRequest();

        request.setCategoryId(999L);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.update(
                        1L,
                        request
                ))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Category not found.");

        verify(productMapper)
                .updateEntity(
                        request,
                        product
                );

        verify(categoryRepository)
                .findById(999L);

        verify(productRepository, never())
                .save(any(Product.class));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_ShouldDeleteProductSuccessfully() {

        // Arrange

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        // Act

        productService.delete(1L);

        // Assert

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
    void delete_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {

        // Arrange

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                999L,
                tenant
        )).thenReturn(Optional.empty());

        // Act & Assert

        assertThatThrownBy(() ->
                productService.delete(999L))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Product not found.");

        verify(productRepository, never())
                .delete(any(Product.class));
    }
}
