package com.pjb.kindergarten_suggestion.handlers;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final AuthService authService;

  @Override
  public void handle(
    HttpServletRequest request,
    HttpServletResponse response,
    AccessDeniedException accessDeniedException
  ) throws IOException, ServletException {
    Optional<User> currentUser = authService.getCurrentUser();

    if (currentUser.isEmpty()) {
      response.sendRedirect(AppRoutes.LOGIN);
      return;
    }
    request.getRequestDispatcher("/403").forward(request, response);
  }
}
