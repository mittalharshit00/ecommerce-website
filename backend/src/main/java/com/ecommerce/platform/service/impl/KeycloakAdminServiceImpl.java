package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.config.KeycloakEndpoints;
import com.ecommerce.platform.config.KeycloakProperties;
import com.ecommerce.platform.dto.keycloak.KeycloakRoleRepresentation;
import com.ecommerce.platform.dto.keycloak.KeycloakTokenResponse;
import com.ecommerce.platform.dto.keycloak.KeycloakUserRepresentation;
import com.ecommerce.platform.service.KeycloakAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakAdminServiceImpl
                implements KeycloakAdminService {

        private static final String ADMIN_ROLE = "ADMIN";

        private final RestClient restClient;

        private final KeycloakProperties keycloakProperties;

        private final KeycloakEndpoints keycloakEndpoints;

        private String adminToken;

        private Instant tokenExpiry;

        private boolean isTokenValid() {

                return adminToken != null
                                && tokenExpiry != null
                                && Instant.now().isBefore(tokenExpiry);

        }

        @Override
        public String getAdminAccessToken() {

                if (isTokenValid()) {
                        return adminToken;
                }

                try {

                        KeycloakTokenResponse response = restClient.post()
                                        .uri(
                                                        keycloakEndpoints.tokenEndpoint())
                                        .contentType(
                                                        MediaType.APPLICATION_FORM_URLENCODED)
                                        .body(
                                                        "grant_type=password"
                                                                        + "&client_id="
                                                                        + keycloakProperties.getClientId()
                                                                        + "&username="
                                                                        + keycloakProperties.getAdmin().getUsername()
                                                                        + "&password="
                                                                        + keycloakProperties.getAdmin().getPassword())
                                        .retrieve()
                                        .body(
                                                        KeycloakTokenResponse.class);

                        if (response == null
                                        || response.getAccessToken() == null) {

                                throw new IllegalStateException(
                                                "Unable to obtain Keycloak admin token.");

                        }

                        adminToken = response.getAccessToken();

                        long expiresIn = response.getExpiresIn() != null
                                        ? response.getExpiresIn()
                                        : 60L;

                        tokenExpiry = Instant.now()
                                        .plusSeconds(
                                                        Math.max(
                                                                        expiresIn - 30,
                                                                        0));

                        return adminToken;

                } catch (RestClientResponseException ex) {

                        throw new IllegalStateException(
                                        "Unable to authenticate with Keycloak.",
                                        ex);

                }

        }

        @Override
        public String getUserIdByUsername(
                        String username) {

                try {

                        KeycloakUserRepresentation[] users = restClient.get()
                                        .uri(
                                                        keycloakEndpoints.usersUri(username))
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + getAdminAccessToken())
                                        .retrieve()
                                        .body(
                                                        KeycloakUserRepresentation[].class);

                        if (users == null || users.length == 0) {

                                throw new IllegalArgumentException(
                                                "Keycloak user not found: " + username);

                        }

                        return users[0].getId();

                } catch (RestClientResponseException ex) {

                        throw new IllegalStateException(
                                        "Unable to retrieve Keycloak user: "
                                                        + username,
                                        ex);

                }

        }

        private KeycloakRoleRepresentation getRealmRole(
                        String roleName) {

                return restClient.get()
                                .uri(
                                                keycloakEndpoints.roleEndpoint(
                                                                roleName))
                                .header(
                                                "Authorization",
                                                "Bearer " + getAdminAccessToken())
                                .retrieve()
                                .body(
                                                KeycloakRoleRepresentation.class);

        }

        @Override
        public String getRealmRoleId(
                        String roleName) {

                KeycloakRoleRepresentation role = getRealmRole(
                                roleName);

                if (role == null || role.getId() == null) {

                        throw new IllegalArgumentException(
                                        "Keycloak role not found: " + roleName);

                }

                return role.getId();

        }

        @Override
        public void assignRealmRole(
                        String username,
                        String roleName) {

                try {

                        String userId = getUserIdByUsername(
                                        username);

                        KeycloakRoleRepresentation role = getRealmRole(
                                        roleName);

                        if (role == null) {

                                throw new IllegalStateException(
                                                "Keycloak role not found.");

                        }

                        restClient.post()
                                        .uri(
                                                        keycloakEndpoints.realmRoleMappingEndpoint(
                                                                        userId))
                                        .header(
                                                        "Authorization",
                                                        "Bearer " + getAdminAccessToken())
                                        .contentType(
                                                        MediaType.APPLICATION_JSON)
                                        .body(
                                                        List.of(role))
                                        .retrieve()
                                        .toBodilessEntity();

                } catch (RestClientResponseException ex) {

                        throw new IllegalStateException(
                                        "Unable to assign Keycloak realm role.",
                                        ex);

                }

        }

}