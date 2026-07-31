package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.request.tenant.AssignTenantAdminRequest;
import com.ecommerce.platform.dto.request.tenant.CreateTenantRequest;
import com.ecommerce.platform.dto.response.DropdownUserResponse;
import com.ecommerce.platform.dto.response.TenantResponse;
import java.util.List;

public interface PlatformAdminService {

    TenantResponse createTenant(
            CreateTenantRequest request
    );

    void assignTenantAdmin(
            Long userId,
            AssignTenantAdminRequest request
    );

    List<DropdownUserResponse> getUsersForAssignment();

    List<TenantResponse> getAllTenants();

}