package com.boot.ordercraft.service;

import com.boot.ordercraft.model.Role;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.RoleRepository;
import com.boot.ordercraft.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdminService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Predefined roles
        String[] roleNames = {
            "ADMIN",
            "PROCUREMENT_OFFICER",
            "PRODUCTION_MANAGER",
            "INVENTORY_MANAGER",
            "SUPPLIER"
        };

        Map<String, Role> roles = new HashMap<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
            roles.put(roleName, role);
        }

        // Recreate admin user with ADMIN role
        userRepository.findByUsername("admin").ifPresent(userRepository::delete);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        admin.setPhoneno(8096845632L);
        admin.setIsActive(true);
        admin.setRole(roles.get("ADMIN"));
        userRepository.save(admin);

        System.out.println("✅ Admin user and roles initialized successfully");
    }
    
    // need to add customer service details
    
    		
}
