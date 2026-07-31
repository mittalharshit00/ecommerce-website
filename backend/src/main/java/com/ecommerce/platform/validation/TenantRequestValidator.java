package com.ecommerce.platform.validation;

import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.exception.ForbiddenException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.repository.TenantRepository;
import com.ecommerce.platform.security.CurrentUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TenantRequestValidator {

        private final CurrentUserService currentUserService;
        private final TenantRepository tenantRepository;

        /**
         * Used for public endpoints.
         *
         * Verifies that the requested tenant exists
         * and is enabled.
         */
        @Transactional(readOnly = true)
        public void validateTenantExists(String tenantDomain) {

                Tenant tenant = tenantRepository
                                .findByDomainIgnoreCase(tenantDomain)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tenant not found."));

                if (!Boolean.TRUE.equals(tenant.getEnabled())) {

                        throw new ForbiddenException(
                                        "Tenant is disabled.");
                }
        }

        /**
         * Used for authenticated endpoints.
         *
         * Verifies that the authenticated user belongs
         * to the tenant specified in the URL.
         */
        @Transactional(readOnly = true)
        public void validateUserTenant(String tenantDomain) {

                Tenant currentTenant = currentUserService.getCurrentTenant();

                if (!Boolean.TRUE.equals(currentTenant.getEnabled())) {

                        throw new ForbiddenException(
                                        "Tenant is disabled.");
                }

                if (!currentTenant.getDomain()
                                .equalsIgnoreCase(tenantDomain)) {

                        throw new ForbiddenException(
                                        "User does not have access to the requested tenant.");
                }

                Tenant requestedTenant = tenantRepository
                                .findByDomainIgnoreCase(tenantDomain)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tenant not found."));

                if (!currentTenant.getId().equals(requestedTenant.getId())) {
                        throw new ForbiddenException(
                                        "User does not have access to the requested tenant.");
                }
        }
}