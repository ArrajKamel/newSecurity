package com.kamel.backend.config;

import com.kamel.backend.model.Role;
import com.kamel.backend.repo.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    @Bean
    public CommandLineRunner dataLoader(RoleRepo repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Role("ROLE_BUYER"));
                repo.save(new Role("ROLE_SELLER"));
                repo.save(new Role("ROLE_BOSS"));
            }
        };
    }
}
