package com.ecommerce.platform.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakRoleRepresentation {

    private String id;

    private String name;

    private String description;

    private Boolean composite;

    private Boolean clientRole;

    private String containerId;

}