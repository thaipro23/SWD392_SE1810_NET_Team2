package com.pjb.kindergarten_suggestion.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.exception.AccountDeletedException;
import com.pjb.kindergarten_suggestion.common.exception.AccountNotActivatedException;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.handlers.CustomAccessDeniedHandler;
import com.pjb.kindergarten_suggestion.handlers.CustomAuthenticationSuccessHandler;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(req -> {
          req.requestMatchers(AppRoutes.privateRoutes.toArray(String[]::new)).authenticated();
          req.requestMatchers(AppRoutes.adminAndSchoolOwnerRoutes.toArray(String[]::new))
              .hasAnyAuthority(Role.ADMIN.name(), Role.SCHOOL_OWNER.name());
          req.requestMatchers(AppRoutes.parentRoutes.toArray(String[]::new)).hasAnyAuthority(Role.PARENT.name(), Role.ADMIN.name(), Role.SCHOOL_OWNER.name());
          req.requestMatchers(AppRoutes.schoolOwnerRoutes.toArray(String[]::new)).hasAnyAuthority(Role.SCHOOL_OWNER.name(), Role.ADMIN.name());
          req.requestMatchers(AppRoutes.adminRoutes.toArray(String[]::new)).hasAuthority(Role.ADMIN.name());
          req.anyRequest().permitAll();
        })
        .formLogin(config -> {
          config.loginPage(AppRoutes.LOGIN);
          config.successHandler(customAuthenticationSuccessHandler);
          config.failureHandler((request, response, exception) -> {
            String redirectUrl = request.getParameter("redirect");
            if (exception.getCause() instanceof AccountNotActivatedException) {
              response.sendRedirect(AppRoutes.LOGIN + "?error=not_activated" + (redirectUrl != null ? "&redirect=" + redirectUrl : ""));
            } else if (exception.getCause() instanceof AccountDeletedException) {
              response.sendRedirect(AppRoutes.LOGIN + "?error=deleted" + (redirectUrl != null ? "&redirect=" + redirectUrl : ""));
            } else {
              response.sendRedirect(AppRoutes.LOGIN + "?error=true" + (redirectUrl != null ? "&redirect=" + redirectUrl : ""));
            }
          });
        })
        .logout(config -> {
          config.logoutUrl(AppRoutes.LOGOUT);
          config.logoutSuccessUrl(AppRoutes.DEFAULT);
        })
        .exceptionHandling(config -> config.accessDeniedHandler(customAccessDeniedHandler))
        .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  UserDetailsService userDetailsService() {
    return username -> {
      User user = userRepository
          .findByUsernameOrEmail(username, username)
          .orElseThrow(() -> new RuntimeException("Username or email is incorrect"));

      if (user.isDeleted()) {
        throw new AccountDeletedException("Account has been deleted");
      }

      if (!user.isActive()) {
        throw new AccountNotActivatedException("Account is not activated");
      }
      return user;
    };
  }
}
