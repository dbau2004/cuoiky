package com.example.coffeeshop.config;

import com.example.coffeeshop.entity.User;
import com.example.coffeeshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("root").isEmpty()) {
            User rootAdmin = new User();
            rootAdmin.setUsername("root");
            rootAdmin.setPassword(passwordEncoder.encode("root123"));
            rootAdmin.setEmail("root@coffeeshop.com");
            rootAdmin.setPhone("123456789");
            rootAdmin.setRole("ADMIN");
            userRepository.save(rootAdmin);
        }
    }
}