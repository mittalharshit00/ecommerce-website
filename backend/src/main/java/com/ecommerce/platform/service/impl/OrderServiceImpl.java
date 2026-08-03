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
import com.ecommerce.platform.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;

        private final ProductRepository productRepository;

        private final OrderMapper orderMapper;

        private final CurrentUserService currentUserService;

        /**
         * User can access only their own orders.
         */
        private Order getOrder(Long id) {

                User user = currentUserService.getCurrentUser();

                return orderRepository
                                .findByIdAndUser(id, user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found."));
        }

        /**
         * Tenant admin can access any order within their tenant.
         */
        private Order getTenantOrder(Long id) {

                Tenant tenant = currentUserService.getCurrentTenant();

                return orderRepository
                                .findByIdAndOrderItems_Product_Tenant(id, tenant)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found."));
        }

        @Override
        public OrderResponse create(CreateOrderRequest request) {

                User user = currentUserService.getCurrentUser();

                Order order = new Order();

                order.setUser(user);

                order.setStatus(OrderStatus.PENDING);

                List<OrderItem> orderItems = new ArrayList<>();

                BigDecimal totalAmount = BigDecimal.ZERO;

                int totalQuantity = 0;

                for (OrderItemRequest itemRequest : request.getItems()) {

                        Product product = productRepository
                                        .findById(itemRequest.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found."));

                        if (product.getQuantity() < itemRequest.getQuantity()) {

                                throw new BadRequestException(
                                                "Insufficient stock for product: "
                                                                + product.getName());
                        }

                        product.setQuantity(
                                        product.getQuantity()
                                                        - itemRequest.getQuantity());

                        OrderItem orderItem = new OrderItem();

                        orderItem.setOrder(order);

                        orderItem.setProduct(product);

                        orderItem.setQuantity(
                                        itemRequest.getQuantity());

                        orderItem.setPrice(
                                        product.getPrice());

                        orderItems.add(orderItem);

                        totalQuantity += itemRequest.getQuantity();

                        totalAmount = totalAmount.add(
                                        product.getPrice().multiply(
                                                        BigDecimal.valueOf(
                                                                        itemRequest.getQuantity())));
                }

                order.setOrderItems(orderItems);

                order.setTotalQuantity(totalQuantity);

                order.setTotalAmount(totalAmount);

                order = orderRepository.save(order);

                return orderMapper.toResponse(order);
        }

        @Override
        @Transactional(readOnly = true)
        public OrderResponse getById(Long id) {

                return orderMapper.toResponse(
                                getOrder(id));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<OrderResponse> getMyOrders(Pageable pageable) {

                return orderRepository
                                .findByUser(
                                                currentUserService.getCurrentUser(),
                                                pageable)
                                .map(orderMapper::toResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<OrderResponse> getAllTenantOrders(
                        Pageable pageable) {

                Tenant tenant = currentUserService.getCurrentTenant();

                return orderRepository
                                .findByOrderItems_Product_Tenant(
                                                tenant,
                                                pageable)
                                .map(orderMapper::toResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public OrderResponse getAdminOrderById(Long id) {

                return orderMapper.toResponse(
                                getTenantOrder(id));
        }

        @Override
        public OrderResponse updateStatus(
                        Long id,
                        OrderStatus status) {

                Order order = getTenantOrder(id);

                order.setStatus(status);

                order = orderRepository.save(order);

                return orderMapper.toResponse(order);
        }
}