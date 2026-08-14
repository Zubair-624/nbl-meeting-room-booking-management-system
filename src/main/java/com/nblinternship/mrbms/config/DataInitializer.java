package com.nblinternship.mrbms.config;

import com.nblinternship.mrbms.entity.Role;
import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.entity.UserRole;
import com.nblinternship.mrbms.repository.RoleRepository;
import com.nblinternship.mrbms.repository.UserRepository;
import com.nblinternship.mrbms.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Roles safely (checks DB before inserting)
        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName("EMPLOYEE");
                    r.setDescription("Standard employee user role");
                    return roleRepository.save(r);
                });

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName("ADMIN");
                    r.setDescription("Administrator role");
                    return roleRepository.save(r);
                });

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName("ROLE_USER");
                    r.setDescription("Standard user role");
                    return roleRepository.save(r);
                });

        // 2. Seed Admin User
        if (userRepository.findByEmail("admin@company.com").isEmpty()) {
            User admin = new User();
            admin.setEmployeeId("EMP001");
            admin.setName("System Admin");
            admin.setEmail("admin@company.com");
            admin.setDepartment("IT");
            admin.setDesignation("Administrator");
            admin.setPhone("1234567890");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));

            User savedAdmin = userRepository.save(admin);

            UserRole adminUserRole = new UserRole();
            adminUserRole.setUser(savedAdmin);
            adminUserRole.setRole(adminRole);
            userRoleRepository.save(adminUserRole);
        }
    }
}