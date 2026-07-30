package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/me")
    public ResponseEntity<TenantResponse> getCurrentTenant() {

        return ResponseEntity.ok(
                tenantService.getCurrentTenant()
        );
    }
}