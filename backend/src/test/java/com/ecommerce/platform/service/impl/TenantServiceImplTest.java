package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.mapper.TenantMapper;
import com.ecommerce.platform.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantMapper tenantMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TenantServiceImpl tenantService;

    @Test
    void getCurrentTenant_shouldReturnTenantResponse() {

        // Arrange
        Tenant tenant = new Tenant();

        TenantResponse expectedResponse = new TenantResponse();

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(tenantMapper.toResponse(tenant))
                .thenReturn(expectedResponse);

        // Act
        TenantResponse actualResponse =
                tenantService.getCurrentTenant();

        // Assert
        assertSame(expectedResponse, actualResponse);

        verify(currentUserService)
                .getCurrentTenant();

        verify(tenantMapper)
                .toResponse(tenant);
    }
}