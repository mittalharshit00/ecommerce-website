package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.response.UserResponse;
import com.ecommerce.platform.entity.Role;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.enums.RoleType;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.UserMapper;
import com.ecommerce.platform.repository.RoleRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.security.CurrentUserService;
import com.ecommerce.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final CurrentUserService currentUserService;

    /**
     * Finds a user by ID within the current tenant.
     */
    private User getUser(Long id) {

        Tenant tenant = currentUserService.getCurrentTenant();

        return userRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }

    /**
     * Synchronizes the currently authenticated Keycloak user
     * with the application's User table.
     *
     * If the user does not exist:
     * - Creates the User
     * - Copies Keycloak user information
     * - Assigns the default tenant
     * - Assigns ADMIN or USER role
     *
     * If the user already exists:
     * - Updates username and email
     */
    @Override
    public UserResponse syncCurrentUser() {

        Jwt jwt = currentUserService.getJwt();

        String keycloakUserId = jwt.getSubject();

        User user = userRepository
                .findByKeycloakUserId(keycloakUserId)
                .orElse(null);

        /*
         * First synchronization:
         * The application user does not exist yet.
         */
        if (user == null) {

            user = new User();

            /*
             * Store Keycloak's unique user ID.
             */
            user.setKeycloakUserId(keycloakUserId);

            /*
             * Copy username from Keycloak.
             */
            user.setUsername(
                    currentUserService.getUsername()
            );

            /*
             * Copy email from Keycloak.
             */
            user.setEmail(
                    currentUserService.getEmail()
            );

            /*
             * During initial synchronization, use
             * the application's default tenant.
             *
             * We cannot use getCurrentTenant() here because
             * the user does not belong to an application
             * tenant yet.
             */
            user.setTenant(
                    currentUserService.getDefaultTenant()
            );

            /*
             * Determine application role from
             * the Keycloak JWT.
             */
            RoleType roleType =
                    currentUserService.hasRole("ADMIN")
                            ? RoleType.ADMIN
                            : RoleType.USER;

            /*
             * Find the corresponding application role.
             */
            Role role =
                    roleRepository.findByName(roleType)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Role not found."
                                    )
                            );

            /*
             * Assign the role to the new user.
             */
            user.getRoles().add(role);

        } else {

            /*
             * User already exists.
             *
             * Synchronize the latest Keycloak
             * username and email.
             */
            user.setUsername(
                    currentUserService.getUsername()
            );

            user.setEmail(
                    currentUserService.getEmail()
            );
        }

        /*
         * Save the new or updated user.
         */
        user = userRepository.save(user);

        /*
         * Convert entity to response DTO.
         */
        return userMapper.toResponse(user);
    }

    /**
     * Returns the currently authenticated application user.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {

        User user = userRepository
                .findByKeycloakUserId(
                        currentUserService.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );

        return userMapper.toResponse(user);
    }

    /**
     * Returns a specific user belonging
     * to the current tenant.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {

        return userMapper.toResponse(
                getUser(id)
        );
    }

    /**
     * Returns all users belonging
     * to the current tenant.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {

        return userRepository
                .findByTenant(
                        currentUserService.getCurrentTenant(),
                        pageable
                )
                .map(userMapper::toResponse);
    }
}