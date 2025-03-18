package com.pjb.kindergarten_suggestion.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
    if (savedRequest != null) {
      response.sendRedirect(savedRequest.getRedirectUrl());
    } else {
      String redirectUrl = request.getParameter("redirect");
      if (redirectUrl != null && !redirectUrl.isEmpty()) {
        response.sendRedirect(redirectUrl);
      } else {
        response.sendRedirect("/");
      }
    }
  }
}
