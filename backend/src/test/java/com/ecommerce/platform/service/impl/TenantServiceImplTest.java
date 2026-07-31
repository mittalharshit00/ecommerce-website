package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.mapper.TenantMapper;
import com.ecommerce.platform.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Tenant tenant;
    private TenantResponse tenantResponse;

    @BeforeEach
    void setUp() {

        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Local Tenant");
        tenant.setDomain("local");
        tenant.setEnabled(true);

        tenantResponse = new TenantResponse(
                1L,
                "Local Tenant",
                "local",
                true
        );
    }

    @Test
    void getCurrentTenant_ShouldReturnCurrentTenant() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(tenantMapper.toResponse(tenant))
                .thenReturn(tenantResponse);

        TenantResponse result =
                tenantService.getCurrentTenant();

        assertThat(result).isEqualTo(tenantResponse);

        verify(currentUserService).getCurrentTenant();
        verify(tenantMapper).toResponse(tenant);
    }
}