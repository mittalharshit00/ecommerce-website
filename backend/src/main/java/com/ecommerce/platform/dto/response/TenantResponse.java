package com.ecommerce.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TenantResponse {

    private Long id;

    private String name;

    private String domain;

    private Boolean enabled;

}