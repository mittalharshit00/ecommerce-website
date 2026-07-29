package com.ecommerce.platform.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakJwtRolesConverter
        implements Converter<Jwt, Collection<SimpleGrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<SimpleGrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        List<String> roles =
                (List<String>) realmAccess.get("roles");

        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}