package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.order.CreateOrderRequest;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.enums.OrderStatus;
import com.ecommerce.platform.validation.TenantRequestValidator;
import com.ecommerce.platform.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenant}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final TenantRequestValidator tenantRequestValidator;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @PathVariable String tenant,

            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.create(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PathVariable String tenant,
            Pageable pageable
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.getMyOrders(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @PathVariable String tenant,
            @PathVariable Long id
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.getById(id)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String tenant,
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.updateStatus(
                        id,
                        status
                )
        );
    }
}