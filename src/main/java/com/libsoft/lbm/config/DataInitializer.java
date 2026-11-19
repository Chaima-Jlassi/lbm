package com.libsoft.lbm.config;

import com.libsoft.lbm.entity.Role;
import com.libsoft.lbm.entity.User;
import com.libsoft.lbm.repository.RoleRepository;
import com.libsoft.lbm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableAsync
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roles, UserRepository users, PasswordEncoder encoder,
                               @Value("${app.admin.default-email}") String adminEmail,
                               @Value("${app.admin.default-password}") String adminPassword) {
        return args -> {
            Role admin = roles.findByLibelle("ADMIN").orElseGet(() -> roles.save(new Role("ADMIN")));
            roles.findByLibelle("BIBLIOTHECAIRE").orElseGet(() -> roles.save(new Role("BIBLIOTHECAIRE")));

            String email = adminEmail.trim().toLowerCase();
            if (!users.existsByEmailIgnoreCase(email)) {
                User u = new User();
                u.setEmail(email);
                u.setPassword(encoder.encode(adminPassword));
                u.setNom("Admin");
                u.setPrenom("Library");
                u.setRoles(new HashSet<>(Set.of(admin)));
                users.save(u);
            }
        };
    }
}
