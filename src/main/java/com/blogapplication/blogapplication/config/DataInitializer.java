package com.blogapplication.blogapplication.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.blogapplication.blogapplication.entity.Role;
import com.blogapplication.blogapplication.entity.User;
import com.blogapplication.blogapplication.repository.RoleRepository;
import com.blogapplication.blogapplication.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Create roles if they don't exist
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }

        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }

        // Create default admin If doesn't exist
        if (!userRepository.existsByUsername("admin")) {

            User admin = new User();
            admin.setName("Administrator");
            admin.setUsername("admin");
            admin.setEmail("admin@blog.com");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_ADMIN").get());
            admin.setRoles(roles);

            userRepository.save(admin);

            System.out.println("====================================");
            System.out.println("Default Admin Created!");
            System.out.println("Username : admin");
            System.out.println("Password : Admin@1234");
            System.out.println("====================================");
        }
    }
}