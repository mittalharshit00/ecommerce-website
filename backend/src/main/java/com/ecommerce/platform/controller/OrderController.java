package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.order.CreateOrderRequest;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.enums.OrderStatus;
import com.ecommerce.platform.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(

            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {

        return ResponseEntity.ok(
                orderService.create(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                orderService.getMyOrders(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getById(id)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {

        return ResponseEntity.ok(
                orderService.updateStatus(
                        id,
                        status
                )
        );
    }
}