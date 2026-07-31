package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.request.tenant.AssignTenantAdminRequest;
import com.ecommerce.platform.dto.request.tenant.CreateTenantRequest;
import com.ecommerce.platform.dto.response.TenantResponse;

public interface PlatformAdminService {

    TenantResponse createTenant(
            CreateTenantRequest request
    );

    void assignTenantAdmin(
            Long userId,
            AssignTenantAdminRequest request
    );

}