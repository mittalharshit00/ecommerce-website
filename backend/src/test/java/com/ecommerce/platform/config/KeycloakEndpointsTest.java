package com.ecommerce.platform.config;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakEndpointsTest {

    @Test
    void buildsUsersUriWithQueryString() {
        KeycloakProperties properties = new KeycloakProperties();
        properties.setServerUrl("http://localhost:8081");
        properties.setRealm("ecommerce");

        KeycloakEndpoints endpoints = new KeycloakEndpoints(properties);

        URI uri = endpoints.usersUri("nike_admin");

        assertThat(uri).isNotNull();
        assertThat(uri.toString())
                .isEqualTo("http://localhost:8081/admin/realms/ecommerce/users?username=nike_admin");
    }
}
