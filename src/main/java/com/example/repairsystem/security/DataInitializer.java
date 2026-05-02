package com.example.repairsystem.security;

import com.example.repairsystem.model.Role;
import com.example.repairsystem.model.User;
import com.example.repairsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Создаём администратора при первом запуске
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                Set.of(Role.ROLE_ADMIN)
            );
            userRepository.save(admin);
            System.out.println("=== Создан администратор: admin / admin123 ===");
        }
    }
}
