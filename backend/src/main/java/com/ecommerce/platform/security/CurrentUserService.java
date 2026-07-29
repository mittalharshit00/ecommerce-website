package com.ecommerce.platform.security;

import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.exception.UnauthorizedException;
import com.ecommerce.platform.repository.TenantRepository;
import com.ecommerce.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    /**
     * Returns the JWT of the currently authenticated user.
     */
    public Jwt getJwt() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Jwt jwt)) {

            throw new UnauthorizedException(
                    "Authentication is required."
            );
        }

        return jwt;
    }

    /**
     * Returns the Keycloak user ID from the JWT subject claim.
     */
    public String getUserId() {

        String subject = getJwt().getSubject();

        if (subject == null || subject.isBlank()) {

            throw new UnauthorizedException(
                    "User id is missing from token."
            );
        }

        return subject;
    }

    /**
     * Returns the username from the Keycloak JWT.
     */
    public String getUsername() {

        String username =
                getJwt().getClaimAsString("preferred_username");

        if (username == null || username.isBlank()) {

            throw new UnauthorizedException(
                    "Username is missing from token."
            );
        }

        return username;
    }

    /**
     * Returns the email from the Keycloak JWT.
     */
    public String getEmail() {

        return getJwt().getClaimAsString("email");
    }

    /**
     * Returns the tenant domain from the Keycloak groups claim.
     *
     * This can be used later if Keycloak is configured
     * to provide tenant groups.
     */
    public String getTenantDomain() {

        List<String> groups =
                getJwt().getClaimAsStringList("groups");

        if (groups == null || groups.isEmpty()) {

            throw new UnauthorizedException(
                    "User does not belong to any tenant."
            );
        }

        return groups.getFirst();
    }

    /**
     * Returns the tenant belonging to the currently
     * authenticated application user.
     *
     * The tenant is resolved from our application's
     * User entity instead of relying on a groups claim
     * inside the Keycloak JWT.
     */
    public Tenant getCurrentTenant() {

        User user = userRepository
                .findByKeycloakUserId(getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );

        Tenant tenant = user.getTenant();

        if (tenant == null) {

            throw new UnauthorizedException(
                    "User does not belong to any tenant."
            );
        }

        return tenant;
    }

    /**
     * Returns the default tenant used during
     * initial Keycloak user synchronization.
     */
    public Tenant getDefaultTenant() {

        return tenantRepository
                .findByDomainIgnoreCase("local")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default tenant not found."
                        )
                );
    }

    /**
     * Checks whether the currently authenticated
     * user has the specified role.
     */
    public boolean hasRole(String role) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role)
                );
    }

    /**
     * Returns the application User associated
     * with the currently authenticated Keycloak user.
     */
    public User getCurrentUser() {

        return userRepository
                .findByKeycloakUserId(getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }
}