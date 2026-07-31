package com.ecommerce.platform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class KeycloakEndpoints {

    private final KeycloakProperties properties;

    public String tokenEndpoint() {

        return properties.getServerUrl()
                + "/realms/master/protocol/openid-connect/token";

    }

    public String usersEndpoint() {

        return properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users";

    }

    public URI usersUri(
            String username
    ) {

        return UriComponentsBuilder
                .fromUriString(
                        properties.getServerUrl()
                )
                .pathSegment(
                        "admin",
                        "realms",
                        properties.getRealm(),
                        "users"
                )
                .queryParam(
                        "username",
                        username
                )
                .build()
                .toUri();

    }

    public String userEndpoint(
            String userId
    ) {

        return usersEndpoint()
                + "/"
                + userId;

    }

    public String roleEndpoint(
            String roleName
    ) {

        return properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/roles/"
                + roleName;

    }

    public String realmRoleMappingEndpoint(
            String userId
    ) {

        return properties.getServerUrl()
                + "/admin/realms/"
                + properties.getRealm()
                + "/users/"
                + userId
                + "/role-mappings/realm";

    }

}