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
         * Finds a user by ID within current tenant.
         */
        private User getUser(Long id) {

                Tenant tenant = currentUserService.getCurrentTenant();

                return userRepository
                                .findByIdAndTenant(id, tenant)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found."));
        }

        /**
         * Synchronizes authenticated Keycloak user
         * with application database.
         */
        @Override
        public UserResponse syncCurrentUser() {

                Jwt jwt = currentUserService.getJwt();

                String keycloakUserId = jwt.getSubject();

                User user = userRepository
                                .findByKeycloakUserId(keycloakUserId)
                                .orElse(null);

                /*
                 * First synchronization
                 */
                if (user == null) {

                        user = new User();

                        user.setKeycloakUserId(
                                        keycloakUserId);

                        user.setUsername(
                                        currentUserService.getUsername());

                        user.setEmail(
                                        currentUserService.getEmail());

                        /*
                         * Tenant assignment
                         *
                         * ADMIN and TENANT are tenant users.
                         *
                         * USER and PLATFORM_ADMIN are global users.
                         */
                        if (currentUserService.hasRole("ADMIN")
                                        || currentUserService.hasRole("TENANT")) {

                                if (user.getTenant() == null) {
                                        user.setTenant(
                                                        currentUserService.getDefaultTenant());
                                }

                        } else {

                                user.setTenant(null);

                        }

                        /*
                         * Determine application role
                         */
                        RoleType roleType;

                        if (currentUserService.hasRole("PLATFORM_ADMIN")) {

                                roleType = RoleType.PLATFORM_ADMIN;

                        } else if (currentUserService.hasRole("ADMIN")) {

                                roleType = RoleType.ADMIN;

                        } else if (currentUserService.hasRole("TENANT")) {

                                roleType = RoleType.TENANT;

                        } else {

                                roleType = RoleType.USER;

                        }

                        Role role = roleRepository
                                        .findByName(roleType)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Role not found."));

                        user.getRoles()
                                        .add(role);

                } else {

                        /*
                         * Existing user:
                         * update Keycloak data
                         */
                        user.setUsername(
                                        currentUserService.getUsername());

                        user.setEmail(
                                        currentUserService.getEmail());

                        /*
                         * Keep tenant assignment consistent
                         *
                         * ADMIN and TENANT -> tenant user
                         *
                         * USER and PLATFORM_ADMIN
                         * -> global user
                         */
                        if (currentUserService.hasRole("ADMIN")
                                        || currentUserService.hasRole("TENANT")) {

                                if (user.getTenant() == null) {
                                        user.setTenant(
                                                        currentUserService.getDefaultTenant());
                                }

                        } else {

                                user.setTenant(null);

                        }
                }

                user = userRepository.save(user);

                return userMapper.toResponse(user);
        }

        /**
         * Returns currently authenticated user.
         */
        @Override
        @Transactional(readOnly = true)
        public UserResponse getCurrentUser() {

                User user = userRepository
                                .findByKeycloakUserId(
                                                currentUserService.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found."));

                return userMapper.toResponse(user);
        }

        /**
         * Returns tenant user by id.
         */
        @Override
        @Transactional(readOnly = true)
        public UserResponse getById(Long id) {

                return userMapper.toResponse(
                                getUser(id));
        }

        /**
         * Returns all users of current tenant.
         */
        @Override
        @Transactional(readOnly = true)
        public Page<UserResponse> getAll(Pageable pageable) {

                return userRepository
                                .findByTenant(
                                                currentUserService.getCurrentTenant(),
                                                pageable)
                                .map(userMapper::toResponse);
        }
}