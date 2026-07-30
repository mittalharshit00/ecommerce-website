package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.enums.OrderStatus;
import com.ecommerce.platform.service.OrderService;
import com.ecommerce.platform.validation.TenantRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenant}/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final TenantRequestValidator tenantRequestValidator;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PathVariable String tenant,
            Pageable pageable
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.getAllTenantOrders(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @PathVariable String tenant,
            @PathVariable Long id
    ) {

        tenantRequestValidator.validateUserTenant(tenant);

        return ResponseEntity.ok(
                orderService.getAdminOrderById(id)
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
                orderService.updateStatus(id, status)
        );
    }
}