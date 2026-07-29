package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.mapper.TenantMapper;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;
    private final CurrentUserService currentUserService;

    @Override
    public TenantResponse getCurrentTenant() {

        return tenantMapper.toResponse(
                currentUserService.getCurrentTenant()
        );
    }

}