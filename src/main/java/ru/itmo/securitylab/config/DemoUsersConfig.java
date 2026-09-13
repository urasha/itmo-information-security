package ru.itmo.securitylab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.securitylab.entity.User;
import ru.itmo.securitylab.repository.UserRepository;

@Configuration
@Profile("demo")
public class DemoUsersConfig {
    @Bean
    CommandLineRunner createDemoUsers(UserRepository users, PasswordEncoder passwords,
                                      @Value("${lab.demo.alice-password}") String alicePassword,
                                      @Value("${lab.demo.bob-password}") String bobPassword) {
        return args -> {
            if (alicePassword.isBlank() || bobPassword.isBlank()) {
                throw new IllegalArgumentException("Demo passwords must be provided through environment variables");
            }
            if (!users.existsByUsername("alice")) {
                users.save(new User("alice", passwords.encode(alicePassword)));
            }
            if (!users.existsByUsername("bob")) {
                users.save(new User("bob", passwords.encode(bobPassword)));
            }
        };
    }
}
