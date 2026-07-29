package com.ecommerce.platform.config;

import com.ecommerce.platform.entity.Role;
import com.ecommerce.platform.entity.Tenant;
import com.ecommerce.platform.enums.RoleType;
import com.ecommerce.platform.repository.RoleRepository;
import com.ecommerce.platform.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;

    private final RoleRepository roleRepository;

    @Value("${application.tenant.default-domain}")
    private String defaultTenantDomain;

    @Value("${application.tenant.default-name}")
    private String defaultTenantName;

    @Override
    @Transactional
    public void run(String... args) {

        initializeTenant();

        initializeRoles();
    }

    private void initializeTenant() {

        if (tenantRepository.existsByDomain(defaultTenantDomain)) {
            return;
        }

        Tenant tenant = new Tenant();

        tenant.setName(defaultTenantName);

        tenant.setDomain(defaultTenantDomain);

        tenant.setEnabled(true);

        tenantRepository.save(tenant);
    }

    private void initializeRoles() {

        if (roleRepository.count() > 0) {
            return;
        }

        Role user = new Role();
        user.setName(RoleType.USER);
        roleRepository.save(user);

        Role admin = new Role();
        admin.setName(RoleType.ADMIN);
        roleRepository.save(admin);
    }
}