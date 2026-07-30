package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.request.order.CreateOrderRequest;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(
            CreateOrderRequest request
    );

    OrderResponse getById(
            Long id
    );

    Page<OrderResponse> getMyOrders(
            Pageable pageable
    );

    Page<OrderResponse> getAllTenantOrders(
            Pageable pageable
    );

    OrderResponse getAdminOrderById(
            Long id
    );

    OrderResponse updateStatus(
            Long id,
            OrderStatus status
    );

}