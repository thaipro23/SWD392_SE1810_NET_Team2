package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AppController {

  private final AuthService authService;

  @GetMapping("/")
  public String home(Model model) {
    Optional<User> user = authService.getCurrentUser();
    if (!user.isPresent() || user.get().getRole().equals(Role.PARENT)) {
      return "redirect:" + AppRoutes.HOME_PAGE;
    }
    if (user.get().getRole().equals(Role.ADMIN)) {
      return "redirect:" + AppRoutes.ADMIN_USER_MANAGEMENT;
    }
    if (user.get().getRole().equals(Role.SCHOOL_OWNER)) {
      return "redirect:" + AppRoutes.SCHOOL_OWNER_SCHOOL;
    }
    return "pages/index";
  }

  @GetMapping(AppRoutes.EXAMPLE)
  public String examplePage(Model model) {
    return "pages/example";
  }
}
