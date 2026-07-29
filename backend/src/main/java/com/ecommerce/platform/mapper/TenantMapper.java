package com.ecommerce.platform.mapper;

import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.entity.Tenant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponse toResponse(Tenant tenant);

}