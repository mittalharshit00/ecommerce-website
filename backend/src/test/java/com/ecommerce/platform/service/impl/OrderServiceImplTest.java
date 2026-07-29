package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.order.CreateOrderRequest;
import com.ecommerce.platform.dto.request.order.OrderItemRequest;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.entity.Order;
import com.ecommerce.platform.entity.Product;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.enums.OrderStatus;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.OrderMapper;
import com.ecommerce.platform.repository.OrderRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.impl.OrderServiceImpl;
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
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Tenant tenant;
    private Product product;

    @BeforeEach
    void setUp() {

        tenant = Tenant.builder()
                .name("Test Tenant")
                .domain("test")
                .enabled(true)
                .build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .keycloakUserId("keycloak-123")
                .enabled(true)
                .tenant(tenant)
                .build();

        product = Product.builder()
                .name("Laptop")
                .description("Test laptop")
                .price(new BigDecimal("1000.00"))
                .quantity(10)
                .tenant(tenant)
                .build();
    }

    @Test
    void create_shouldCreateOrderSuccessfully() {

        OrderItemRequest itemRequest = new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();

        request.setItems(List.of(itemRequest));

        Order savedOrder = new Order();

        OrderResponse response = new OrderResponse();

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderMapper.toResponse(savedOrder))
                .thenReturn(response);

        OrderResponse result =
                orderService.create(request);

        assertNotNull(result);

        assertEquals(
                8,
                product.getQuantity()
        );

        verify(productRepository)
                .findByIdAndTenant(1L, tenant);

        verify(orderRepository)
                .save(any(Order.class));

        verify(orderMapper)
                .toResponse(savedOrder);
    }

    @Test
    void create_shouldThrowExceptionWhenProductNotFound() {

        OrderItemRequest itemRequest = new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();

        request.setItems(List.of(itemRequest));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.create(request)
                );

        assertEquals(
                "Product not found.",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void create_shouldThrowExceptionWhenStockIsInsufficient() {

        product.setQuantity(1);

        OrderItemRequest itemRequest = new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5);

        CreateOrderRequest request = new CreateOrderRequest();

        request.setItems(List.of(itemRequest));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(productRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(product));

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> orderService.create(request)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Insufficient stock")
        );

        verify(orderRepository, never())
                .save(any(Order.class));

        assertEquals(
                1,
                product.getQuantity()
        );
    }

    @Test
    void getById_shouldReturnOrderSuccessfully() {

        Order order = new Order();

        OrderResponse response = new OrderResponse();

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        OrderResponse result =
                orderService.getById(1L);

        assertNotNull(result);

        assertSame(
                response,
                result
        );

        verify(orderRepository)
                .findByIdAndUser(1L, user);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void getById_shouldThrowExceptionWhenOrderNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.getById(1L)
                );

        assertEquals(
                "Order not found.",
                exception.getMessage()
        );

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void getMyOrders_shouldReturnOrdersSuccessfully() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Order order = new Order();

        OrderResponse response = new OrderResponse();

        Page<Order> orderPage =
                new PageImpl<>(List.of(order));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByUser(
                user,
                pageable
        )).thenReturn(orderPage);

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        Page<OrderResponse> result =
                orderService.getMyOrders(pageable);

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertSame(
                response,
                result.getContent().getFirst()
        );

        verify(orderRepository)
                .findByUser(user, pageable);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void updateStatus_shouldUpdateOrderSuccessfully() {

        Order order = new Order();

        order.setStatus(OrderStatus.PENDING);

        OrderResponse response = new OrderResponse();

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(response);

        OrderResponse result =
                orderService.updateStatus(
                        1L,
                        OrderStatus.CONFIRMED
                );

        assertNotNull(result);

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        verify(orderRepository)
                .findByIdAndUser(1L, user);

        verify(orderRepository)
                .save(order);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void updateStatus_shouldThrowExceptionWhenOrderNotFound() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> orderService.updateStatus(
                                1L,
                                OrderStatus.CONFIRMED
                        )
                );

        assertEquals(
                "Order not found.",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}