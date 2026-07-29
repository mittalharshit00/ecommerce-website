package com.ecommerce.platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakJwtRolesConverter roleConverter;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        return new JwtAuthenticationToken(
                jwt,
                roleConverter.convert(jwt),
                jwt.getClaimAsString("preferred_username")
        );
    }

}