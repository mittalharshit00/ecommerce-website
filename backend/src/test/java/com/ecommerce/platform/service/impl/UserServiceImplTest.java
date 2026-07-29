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
import com.ecommerce.platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserServiceImpl userService;

    private Tenant tenant;
    private User user;
    private Role role;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        tenant = Tenant.builder()
                .name("Test Tenant")
                .domain("test")
                .enabled(true)
                .build();

        role = Role.builder()
                .name(RoleType.USER)
                .build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .keycloakUserId("keycloak-user-123")
                .enabled(true)
                .tenant(tenant)
                .build();

        userResponse = new UserResponse();
    }

    @Test
    void syncCurrentUser_shouldCreateNewUserSuccessfully() {

        when(currentUserService.getJwt())
                .thenReturn(jwt);

        when(jwt.getSubject())
                .thenReturn("keycloak-user-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-user-123"
        )).thenReturn(Optional.empty());

        when(currentUserService.getUsername())
                .thenReturn("testuser");

        when(currentUserService.getEmail())
                .thenReturn("test@example.com");

        when(currentUserService.getDefaultTenant())
                .thenReturn(tenant);

        when(currentUserService.hasRole("ADMIN"))
                .thenReturn(false);

        when(roleRepository.findByName(RoleType.USER))
                .thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(userMapper.toResponse(any(User.class)))
                .thenReturn(userResponse);

        UserResponse result =
                userService.syncCurrentUser();

        assertNotNull(result);

        verify(userRepository)
                .findByKeycloakUserId("keycloak-user-123");

        verify(currentUserService)
                .getDefaultTenant();

        verify(roleRepository)
                .findByName(RoleType.USER);

        verify(userRepository)
                .save(any(User.class));

        verify(userMapper)
                .toResponse(any(User.class));
    }

    @Test
    void syncCurrentUser_shouldAssignAdminRoleForAdmin() {

        when(currentUserService.getJwt())
                .thenReturn(jwt);

        when(jwt.getSubject())
                .thenReturn("keycloak-admin-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-admin-123"
        )).thenReturn(Optional.empty());

        when(currentUserService.getUsername())
                .thenReturn("admin");

        when(currentUserService.getEmail())
                .thenReturn("admin@example.com");

        when(currentUserService.getDefaultTenant())
                .thenReturn(tenant);

        when(currentUserService.hasRole("ADMIN"))
                .thenReturn(true);

        Role adminRole = Role.builder()
                .name(RoleType.ADMIN)
                .build();

        when(roleRepository.findByName(RoleType.ADMIN))
                .thenReturn(Optional.of(adminRole));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(userMapper.toResponse(any(User.class)))
                .thenReturn(userResponse);

        UserResponse result =
                userService.syncCurrentUser();

        assertNotNull(result);

        verify(currentUserService)
                .hasRole("ADMIN");

        verify(roleRepository)
                .findByName(RoleType.ADMIN);

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void syncCurrentUser_shouldUpdateExistingUser() {

        User existingUser = User.builder()
                .username("oldusername")
                .email("old@example.com")
                .keycloakUserId("keycloak-user-123")
                .enabled(true)
                .tenant(tenant)
                .build();

        when(currentUserService.getJwt())
                .thenReturn(jwt);

        when(jwt.getSubject())
                .thenReturn("keycloak-user-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-user-123"
        )).thenReturn(Optional.of(existingUser));

        when(currentUserService.getUsername())
                .thenReturn("newusername");

        when(currentUserService.getEmail())
                .thenReturn("new@example.com");

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        when(userMapper.toResponse(existingUser))
                .thenReturn(userResponse);

        UserResponse result =
                userService.syncCurrentUser();

        assertNotNull(result);

        assertEquals(
                "newusername",
                existingUser.getUsername()
        );

        assertEquals(
                "new@example.com",
                existingUser.getEmail()
        );

        verify(userRepository)
                .save(existingUser);

        verify(userMapper)
                .toResponse(existingUser);

        verify(currentUserService, never())
                .getDefaultTenant();

        verify(roleRepository, never())
                .findByName(any(RoleType.class));
    }

    @Test
    void syncCurrentUser_shouldThrowExceptionWhenRoleNotFound() {

        when(currentUserService.getJwt())
                .thenReturn(jwt);

        when(jwt.getSubject())
                .thenReturn("keycloak-user-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-user-123"
        )).thenReturn(Optional.empty());

        when(currentUserService.getUsername())
                .thenReturn("testuser");

        when(currentUserService.getEmail())
                .thenReturn("test@example.com");

        when(currentUserService.getDefaultTenant())
                .thenReturn(tenant);

        when(currentUserService.hasRole("ADMIN"))
                .thenReturn(false);

        when(roleRepository.findByName(RoleType.USER))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.syncCurrentUser()
                );

        assertEquals(
                "Role not found.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserSuccessfully() {

        when(currentUserService.getUserId())
                .thenReturn("keycloak-user-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-user-123"
        )).thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.getCurrentUser();

        assertNotNull(result);

        assertSame(
                userResponse,
                result
        );

        verify(userRepository)
                .findByKeycloakUserId("keycloak-user-123");

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void getCurrentUser_shouldThrowExceptionWhenUserNotFound() {

        when(currentUserService.getUserId())
                .thenReturn("keycloak-user-123");

        when(userRepository.findByKeycloakUserId(
                "keycloak-user-123"
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.getCurrentUser()
                );

        assertEquals(
                "User not found.",
                exception.getMessage()
        );

        verify(userMapper, never())
                .toResponse(any(User.class));
    }

    @Test
    void getById_shouldReturnUserSuccessfully() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(userRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.getById(1L);

        assertNotNull(result);

        assertSame(
                userResponse,
                result
        );

        verify(userRepository)
                .findByIdAndTenant(1L, tenant);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void getById_shouldThrowExceptionWhenUserNotFound() {

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(userRepository.findByIdAndTenant(
                1L,
                tenant
        )).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.getById(1L)
                );

        assertEquals(
                "User not found.",
                exception.getMessage()
        );

        verify(userMapper, never())
                .toResponse(any(User.class));
    }

    @Test
    void getAll_shouldReturnUsersSuccessfully() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(List.of(user));

        when(currentUserService.getCurrentTenant())
                .thenReturn(tenant);

        when(userRepository.findByTenant(
                tenant,
                pageable
        )).thenReturn(userPage);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        Page<UserResponse> result =
                userService.getAll(pageable);

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertSame(
                userResponse,
                result.getContent().getFirst()
        );

        verify(userRepository)
                .findByTenant(tenant, pageable);

        verify(userMapper)
                .toResponse(user);
    }
}