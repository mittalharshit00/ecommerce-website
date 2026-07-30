package com.ecommerce.platform.security;

import com.ecommerce.platform.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class UserSyncFilter extends OncePerRequestFilter {


    private final UserSyncService userSyncService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {


        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if(authentication instanceof JwtAuthenticationToken jwtAuth) {


            Jwt jwt = jwtAuth.getToken();


            userSyncService.syncUser(jwt);
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}