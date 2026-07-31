package com.ecommerce.platform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;

    private String realm;


    private String clientId;

    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Admin {

        private String username;

        private String password;

    }

}