package com.ecommerce.platform.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakUserRepresentation {

    private String id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private Boolean emailVerified;

    private Long createdTimestamp;

    private List<String> realmRoles;

}