package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.request.tenant.AssignTenantAdminRequest;
import com.ecommerce.platform.dto.request.tenant.CreateTenantRequest;
import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.service.PlatformAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class PlatformAdminController {

    private final PlatformAdminService platformAdminService;

    @PostMapping("/tenants")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse createTenant(
            @Valid
            @RequestBody
            CreateTenantRequest request
    ) {

        return platformAdminService.createTenant(request);
    }

    @PutMapping("/users/{userId}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignTenantAdmin(

            @PathVariable
            Long userId,

            @Valid
            @RequestBody
            AssignTenantAdminRequest request

    ) {

        platformAdminService.assignTenantAdmin(
                userId,
                request
        );
    }
}