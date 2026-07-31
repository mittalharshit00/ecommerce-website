
package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.request.tenant.AssignTenantAdminRequest;
import com.ecommerce.platform.dto.request.tenant.CreateTenantRequest;
import com.ecommerce.platform.dto.response.DropdownUserResponse;
import com.ecommerce.platform.dto.response.TenantResponse;
import com.ecommerce.platform.entity.Role;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.enums.RoleType;
import com.ecommerce.platform.exception.ConflictException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.mapper.TenantMapper;
import com.ecommerce.platform.repository.RoleRepository;
import com.ecommerce.platform.repository.TenantRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.service.KeycloakAdminService;
import com.ecommerce.platform.service.PlatformAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PlatformAdminServiceImpl
                implements PlatformAdminService {



        private final TenantRepository tenantRepository;

        private final UserRepository userRepository;

        private final RoleRepository roleRepository;

        private final TenantMapper tenantMapper;

        private final KeycloakAdminService keycloakAdminService;





        @Override
        public TenantResponse createTenant(
                        CreateTenantRequest request) {


                if (tenantRepository.existsByNameIgnoreCase(request.name())) {

                        throw new ConflictException(
                                        "Tenant name already exists.");

                }



                if (tenantRepository.existsByDomainIgnoreCase(request.domain())) {

                        throw new ConflictException(
                                        "Tenant domain already exists.");

                }




                Tenant tenant = Tenant.builder()
                                .name(request.name().trim())
                                .domain(request.domain().trim().toLowerCase())
                                .enabled(true)
                                .build();



                tenant = tenantRepository.save(tenant);



                return tenantMapper.toResponse(tenant);

        }







        @Override
        public void assignTenantAdmin(
                        Long userId,
                        AssignTenantAdminRequest request) {



                User user = userRepository.findById(userId)
                                .orElseThrow(() ->
                                                new ResourceNotFoundException(
                                                        "User not found."));





                Tenant tenant = tenantRepository.findById(
                                request.tenantId())
                                .orElseThrow(() ->
                                                new ResourceNotFoundException(
                                                        "Tenant not found."));






                Role adminRole = roleRepository.findByName(
                                RoleType.ADMIN)
                                .orElseThrow(() ->
                                                new ResourceNotFoundException(
                                                        "ADMIN role not found."));







                boolean alreadyAdmin =
                                user.getRoles()
                                        .stream()
                                        .anyMatch(role ->
                                                role.getName() == RoleType.ADMIN);







                /*
                 * Update Keycloak first.
                 * If this fails, database transaction
                 * will not commit.
                 */
                keycloakAdminService.assignRealmRole(
                                user.getUsername(),
                                RoleType.ADMIN.name());






                user.setTenant(tenant);




                if (!alreadyAdmin) {

                        user.getRoles()
                                .add(adminRole);

                }





                userRepository.save(user);

        }









        @Override
        public List<DropdownUserResponse> getUsersForAssignment() {



                return userRepository
                                .findByTenantIsNull()
                                .stream()
                                .map(user ->
                                        new DropdownUserResponse(
                                                user.getId(),
                                                user.getUsername(),
                                                user.getEmail()
                                        )
                                )
                                .toList();

        }









        @Override
        public List<TenantResponse> getAllTenants() {



                return tenantRepository
                                .findAll()
                                .stream()
                                .map(tenant ->
                                        new TenantResponse(
                                                tenant.getId(),
                                                tenant.getName(),
                                                tenant.getDomain(),
                                                tenant.getEnabled()
                                        )
                                )
                                .toList();

        }


}

