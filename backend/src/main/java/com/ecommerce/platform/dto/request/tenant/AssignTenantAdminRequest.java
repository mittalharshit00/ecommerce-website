package com.ecommerce.platform.dto.request.tenant;

import jakarta.validation.constraints.NotNull;

public record AssignTenantAdminRequest(

        @NotNull(message = "Tenant is required")
        Long tenantId

) {
}       