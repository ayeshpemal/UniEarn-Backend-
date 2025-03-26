package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.Role;
import com.finalproject.uni_earn.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultAdmin() {
        String defaultAdminUsername = "admin";
        String defaultAdminEmail = "admin@uniearn.com";
        String defaultAdminPassword = "Admin@123";

        Optional<User> existingAdmin = userRepository.findByEmailAndRole(defaultAdminEmail, Role.ADMIN);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUserName(defaultAdminUsername);
            admin.setEmail(defaultAdminEmail);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setRole(Role.ADMIN);
            admin.setVerified(true);
            userRepository.save(admin);
            System.out.println("Default admin created: " + defaultAdminEmail);
        } else {
            System.out.println("Admin already exists.");
        }
    }
}
