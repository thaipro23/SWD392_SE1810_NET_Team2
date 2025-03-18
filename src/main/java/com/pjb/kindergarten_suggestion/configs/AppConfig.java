package com.pjb.kindergarten_suggestion.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  @Value("${spring.security.user.name}")
  private String initialAdminUsername;

  @Value("${spring.security.user.email}")
  private String initialAdminEmail;

  @Value("${spring.security.user.password}")
  private String initialAdminPassword;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {
      User initialAdmin = userRepository.findByUsernameOrEmail(initialAdminUsername, initialAdminEmail).orElse(null);

      if (initialAdmin == null) {
        initialAdmin = new User();
      }
      initialAdmin.setFullname("Admin");
      initialAdmin.setUsername(initialAdminUsername);
      initialAdmin.setEmail(initialAdminEmail);
      initialAdmin.setPassword(passwordEncoder.encode(initialAdminPassword));
      initialAdmin.setRole(Role.ADMIN);
      initialAdmin.setActive(true);
      initialAdmin.setDeleted(false);
      userRepository.save(initialAdmin);
    };
  }
}
