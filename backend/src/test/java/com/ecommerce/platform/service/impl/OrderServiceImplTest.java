
package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.order.CreateOrderRequest;
import com.ecommerce.platform.dto.request.order.OrderItemRequest;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.entity.Order;
import com.ecommerce.platform.entity.OrderItem;
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

import java.math.BigDecimal;
import java.util.Arrays;
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
    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("john");

        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Local");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(10);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalQuantity(2);
        order.setTotalAmount(new BigDecimal("200.00"));

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
    }

    @Test
    void getAllTenantOrders_ShouldFetchOrdersForTenantProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orderPage = new PageImpl<>(
                List.of(order),
                pageable,
                1
        );

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByOrderItems_Product_Tenant(
                tenant,
                pageable
        ))
                .thenReturn(orderPage);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> result =
                orderService.getAllTenantOrders(pageable);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .isEqualTo(orderResponse);

        verify(orderRepository)
                .findByOrderItems_Product_Tenant(
                        tenant,
                        pageable
                );

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void create_ShouldCreateOrderSuccessfully() {

        OrderItemRequest itemRequest =
                new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setItems(List.of(itemRequest));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.create(request);

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        assertThat(product.getQuantity())
                .isEqualTo(8);

        verify(currentUserService)
                .getCurrentUser();

        verify(productRepository)
                .findById(1L);

        verify(orderRepository)
                .save(any(Order.class));

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void create_ShouldSetCorrectOrderValues() {

        OrderItemRequest itemRequest =
                new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(3);

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setItems(List.of(itemRequest));

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderMapper.toResponse(any(Order.class)))
                .thenReturn(orderResponse);

        orderService.create(request);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository)
                .save(orderCaptor.capture());

        Order savedOrder =
                orderCaptor.getValue();

        assertThat(savedOrder.getUser())
                .isEqualTo(user);

        assertThat(savedOrder.getStatus())
                .isEqualTo(OrderStatus.PENDING);

        assertThat(savedOrder.getTotalQuantity())
                .isEqualTo(3);

        assertThat(savedOrder.getTotalAmount())
                .isEqualByComparingTo(
                        new BigDecimal("300.00")
                );

        assertThat(savedOrder.getOrderItems())
                .hasSize(1);

        OrderItem savedItem =
                savedOrder.getOrderItems().get(0);

        assertThat(savedItem.getOrder())
                .isEqualTo(savedOrder);

        assertThat(savedItem.getProduct())
                .isEqualTo(product);

        assertThat(savedItem.getQuantity())
                .isEqualTo(3);

        assertThat(savedItem.getPrice())
                .isEqualByComparingTo(
                        new BigDecimal("100.00")
                );
    }

    @Test
    void create_ShouldCalculateTotalForMultipleItems() {

        Product secondProduct =
                new Product();

        secondProduct.setId(2L);
        secondProduct.setName("Mouse");
        secondProduct.setPrice(
                new BigDecimal("50.00")
        );
        secondProduct.setQuantity(20);

        OrderItemRequest firstItem =
                new OrderItemRequest();

        firstItem.setProductId(1L);
        firstItem.setQuantity(2);

        OrderItemRequest secondItem =
                new OrderItemRequest();

        secondItem.setProductId(2L);
        secondItem.setQuantity(3);

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setItems(
                List.of(firstItem, secondItem)
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(secondProduct));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderMapper.toResponse(any(Order.class)))
                .thenReturn(orderResponse);

        orderService.create(request);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository)
                .save(orderCaptor.capture());

        Order savedOrder =
                orderCaptor.getValue();

        assertThat(savedOrder.getTotalQuantity())
                .isEqualTo(5);

        assertThat(savedOrder.getTotalAmount())
                .isEqualByComparingTo(
                        new BigDecimal("350.00")
                );

        assertThat(product.getQuantity())
                .isEqualTo(8);

        assertThat(secondProduct.getQuantity())
                .isEqualTo(17);

        assertThat(savedOrder.getOrderItems())
                .hasSize(2);
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {

        OrderItemRequest itemRequest =
                new OrderItemRequest();

        itemRequest.setProductId(999L);
        itemRequest.setQuantity(1);

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setItems(
                List.of(itemRequest)
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.create(request))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Product not found.");

        verify(productRepository)
                .findById(999L);

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void create_ShouldThrowBadRequestException_WhenStockIsInsufficient() {

        OrderItemRequest itemRequest =
                new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(20);

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setItems(
                List.of(itemRequest)
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                orderService.create(request))
                .isInstanceOf(
                        BadRequestException.class
                )
                .hasMessage(
                        "Insufficient stock for product: Laptop"
                );

        assertThat(product.getQuantity())
                .isEqualTo(10);

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void getById_ShouldReturnOrderSuccessfully() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.getById(1L);

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(currentUserService)
                .getCurrentUser();

        verify(orderRepository)
                .findByIdAndUser(1L, user);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenOrderDoesNotExist() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByIdAndUser(
                1L,
                user
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.getById(1L))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Order not found.");

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void getMyOrders_ShouldReturnUserOrders() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Order> orderPage =
                new PageImpl<>(
                        List.of(order),
                        pageable,
                        1
                );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByUser(
                eq(user),
                eq(pageable)
        )).thenReturn(orderPage);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> result =
                orderService.getMyOrders(pageable);

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .isEqualTo(orderResponse);

        verify(orderRepository)
                .findByUser(user, pageable);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void getMyOrders_ShouldReturnEmptyPage_WhenUserHasNoOrders() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Order> emptyPage =
                Page.empty(pageable);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(orderRepository.findByUser(
                eq(user),
                eq(pageable)
        )).thenReturn(emptyPage);

        Page<OrderResponse> result =
                orderService.getMyOrders(pageable);

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isZero();

        assertThat(result.getContent())
                .isEmpty();

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void getAllTenantOrders_ShouldReturnTenantOrders() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Order> orderPage =
                new PageImpl<>(
                        List.of(order),
                        pageable,
                        1
                );

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByOrderItems_Product_Tenant(
                eq(tenant),
                eq(pageable)
        )).thenReturn(orderPage);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        Page<OrderResponse> result =
                orderService.getAllTenantOrders(
                        pageable
                );

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .isEqualTo(orderResponse);

        verify(currentUserService)
                .getCurrentTenant();

        verify(orderRepository)
                .findByOrderItems_Product_Tenant(
                        tenant,
                        pageable
                );

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void getAllTenantOrders_ShouldReturnEmptyPage_WhenTenantHasNoOrders() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Order> emptyPage =
                Page.empty(pageable);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByOrderItems_Product_Tenant(
                eq(tenant),
                eq(pageable)
        )).thenReturn(emptyPage);

        Page<OrderResponse> result =
                orderService.getAllTenantOrders(
                        pageable
                );

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotalElements())
                .isZero();

        assertThat(result.getContent())
                .isEmpty();

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void getAdminOrderById_ShouldReturnTenantOrderSuccessfully() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByIdAndOrderItems_Product_Tenant(
                1L,
                tenant
        )).thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.getAdminOrderById(1L);

        assertThat(result)
                .isNotNull();

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(currentUserService)
                .getCurrentTenant();

        verify(orderRepository)
                .findByIdAndOrderItems_Product_Tenant(
                        1L,
                        tenant
                );

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void getAdminOrderById_ShouldThrowResourceNotFoundException_WhenOrderNotInTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByIdAndOrderItems_Product_Tenant(
                1L,
                tenant
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.getAdminOrderById(1L))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Order not found.");

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }

    @Test
    void updateStatus_ShouldUpdateOrderStatusSuccessfully() {

        /*
         * We don't assume a specific status such as SHIPPED,
         * because the actual OrderStatus enum in the project
         * may contain different values.
         *
         * We select any status that is different from PENDING.
         */
        OrderStatus newStatus =
                Arrays.stream(OrderStatus.values())
                        .filter(status ->
                                status != OrderStatus.PENDING)
                        .findFirst()
                        .orElse(OrderStatus.PENDING);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByIdAndOrderItems_Product_Tenant(
                1L,
                tenant
        )).thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result =
                orderService.updateStatus(
                        1L,
                        newStatus
                );

        assertThat(result)
                .isNotNull();

        assertThat(order.getStatus())
                .isEqualTo(newStatus);

        verify(currentUserService)
                .getCurrentTenant();

        verify(orderRepository)
                .findByIdAndOrderItems_Product_Tenant(
                        1L,
                        tenant
                );

        verify(orderRepository)
                .save(order);

        verify(orderMapper)
                .toResponse(order);
    }

    @Test
    void updateStatus_ShouldThrowResourceNotFoundException_WhenOrderNotFound() {

        OrderStatus newStatus =
                Arrays.stream(OrderStatus.values())
                        .findFirst()
                        .orElse(OrderStatus.PENDING);

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(orderRepository.findByIdAndOrderItems_Product_Tenant(
                1L,
                tenant
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.updateStatus(
                        1L,
                        newStatus
                ))
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage("Order not found.");

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(orderMapper, never())
                .toResponse(any(Order.class));
    }
}

