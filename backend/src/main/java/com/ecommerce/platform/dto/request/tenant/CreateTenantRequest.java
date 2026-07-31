package com.ecommerce.platform.dto.request.tenant;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(

    @NotBlank(message = "Tenant name is required")
    String name,

    @NotBlank(message = "Tenant domain is required")
    String domain

) {
}