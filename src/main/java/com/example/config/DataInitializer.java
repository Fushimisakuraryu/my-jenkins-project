package com.example.config;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Alice", "alice@example.com"));
            userRepository.save(new User("Bob", "bob@example.com"));
            userRepository.save(new User("Charlie", "charlie@example.com"));
        }
    }
}
