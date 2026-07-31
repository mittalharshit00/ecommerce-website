package com.ecommerce.platform.controller;

import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.exception.ForbiddenException;
import com.ecommerce.platform.repository.TenantRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.validation.TenantRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantRequestValidatorTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantRequestValidator tenantRequestValidator;

    @Test
    void validateUserTenantRejectsDifferentTenant() {
        Tenant currentTenant = new Tenant();
        currentTenant.setId(1L);
        currentTenant.setDomain("tenant-a");
        currentTenant.setEnabled(true);

        Tenant requestedTenant = new Tenant();
        requestedTenant.setId(2L);
        requestedTenant.setDomain("tenant-a");
        requestedTenant.setEnabled(true);

        when(currentUserService.getCurrentTenant()).thenReturn(currentTenant);
        when(tenantRepository.findByDomainIgnoreCase("tenant-a"))
                .thenReturn(Optional.of(requestedTenant));

        assertThrows(ForbiddenException.class, () ->
                tenantRequestValidator.validateUserTenant("tenant-a"));
    }
}
