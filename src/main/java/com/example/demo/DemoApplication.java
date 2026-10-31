package com.example.demo;

import com.example.demo.enums.RoleType;
import com.example.demo.models.User;
import com.example.demo.models.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Automatically runs when the server starts up
    @Bean
    CommandLineRunner initDefaultAccounts(UserRepository userRepository) {
        return args -> {
            // Create default Admin if 'admin' doesn't exist yet
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", "admin123", RoleType.ADMIN);
                userRepository.save(admin);
                System.out.println("Default ADMIN created: username='admin', password='admin123'");
            }

            // Create default test User if 'test' doesn't exist yet
            if (!userRepository.existsByUsername("test")) {
                User testUser = new User("test", "test@example.com", "1234", RoleType.USER);
                userRepository.save(testUser);
                System.out.println("Default USER created: username='test', password='1234'");
            }
        };
    }
}