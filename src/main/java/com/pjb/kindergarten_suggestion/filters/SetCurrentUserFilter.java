package com.pjb.kindergarten_suggestion.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;

@Component
@RequiredArgsConstructor
public class SetCurrentUserFilter extends OncePerRequestFilter {

  private final AuthService authService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    Optional<User> currentUser = authService.getCurrentUser();
    if (currentUser.isPresent()) {
      request.setAttribute("currentUser", currentUser.get());
    }
    String currentUrl = request.getRequestURI();
    request.setAttribute("currentUrl", currentUrl);
    filterChain.doFilter(request, response);
  }
}
