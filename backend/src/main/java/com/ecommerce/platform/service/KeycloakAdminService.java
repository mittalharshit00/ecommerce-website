package com.ecommerce.platform.service;

public interface KeycloakAdminService {

    /**
     * Obtains an admin access token from Keycloak.
     *
     * @return Bearer access token
     */
    String getAdminAccessToken();

    /**
     * Finds a Keycloak user ID by username.
     *
     * @param username Keycloak username
     * @return Keycloak user ID
     */
    String getUserIdByUsername(
            String username
    );

    /**
     * Finds the ID of a realm role.
     *
     * @param roleName Realm role name
     * @return Realm role ID
     */
    String getRealmRoleId(
            String roleName
    );

    /**
     * Assigns a realm role to a Keycloak user.
     *
     * @param username Username of the user
     * @param roleName Realm role to assign
     */
    void assignRealmRole(
            String username,
            String roleName
    );

}