package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.exception.UserAlreadyExistsException;
import com.pjb.kindergarten_suggestion.dto.RegisterDTO;
import com.pjb.kindergarten_suggestion.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.pjb.kindergarten_suggestion.services.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;
  private final AuthService authService;
  private final BCryptPasswordEncoder passwordEncoder;

  @GetMapping(AppRoutes.LOGIN)
  public String loginPage(Model model) {
    model.addAttribute("pageTitle", "Login");
    return "pages/auth/auth-login-basic";
  }

  @GetMapping(AppRoutes.FORGOT_PASSWORD)
  public String showForgotPasswordForm() {
    return "pages/auth/auth-forgot-password";
  }

  @PostMapping(AppRoutes.FORGOT_PASSWORD)
  public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
    try {
      userService.sendResetPasswordEmail(email, request);
      redirectAttributes.addFlashAttribute("successMessage",
          "We've sent an email with the link to reset your password.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "The email address doesn’t exist. Please try again.");
    }
    return "redirect:" + AppRoutes.FORGOT_PASSWORD;
  }

  @GetMapping(AppRoutes.RESET_PASSWORD)
  public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token, Model model) {
    model.addAttribute("pageTitle", "Reset Password");
    if (token == null || !userService.isTokenValid(token)) {
      model.addAttribute("errorMessage", "This link has expired. Please go back to Homepage and try again.");
      return "pages/auth/auth-reset-password";
    }
    model.addAttribute("token", token);
    return "pages/auth/auth-reset-password";
  }

  @PostMapping(AppRoutes.RESET_PASSWORD)
  public String processResetPassword(@RequestParam("token") String token,
      @RequestParam("password") String password,
      @RequestParam("repassword") String repassword,
      RedirectAttributes redirectAttributes) {
    if (!password.equals(repassword)) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Password and Confirm password don’t match. Please try again.");
      return "redirect:" + AppRoutes.RESET_PASSWORD + "?token=" + token;
    }
    if (!userService.isValidPassword(password)) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Password must contain at least one number, one numeral, and seven characters.");
      return "redirect:" + AppRoutes.RESET_PASSWORD + "?token=" + token;
    }

    try {
      userService.updatePasswordReset(token, password);
      return "redirect:" + AppRoutes.LOGIN;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:" + AppRoutes.RESET_PASSWORD + "?token=" + token;
    }
  }

  @GetMapping(AppRoutes.REGISTER)
  public String getRegisterPage(Model model) {
    model.addAttribute("pageTitle", "Register");
    if (model.containsAttribute("error")) {
      model.addAttribute("errorMessage", model.getAttribute("error"));
    }
    model.addAttribute("user", new RegisterDTO());
    return "pages/auth/auth-register";
  }

  @PostMapping(AppRoutes.REGISTER)
  public String registerPost(@Valid @ModelAttribute("user") RegisterDTO registerDTO,
      BindingResult result, Model model, RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
      if (registerDTO.getPhone() == null) {
          result.rejectValue("phone", "error.phone", "Phone number is required");
      }
      if (userService.isExistPhone(registerDTO.getPhone())) {
          result.rejectValue("phone", "error.phone", "Phone number already exist!");
      }
    if (result.hasErrors()) {
      return "pages/auth/auth-register";
    }
    try {
      User user = this.authService.RegisterDTOtoUser(registerDTO);
      String hashPassword = this.passwordEncoder.encode(user.getPassword());
      user.setPassword(hashPassword);
      this.authService.handleSaveUsers(user);
      try {
        userService.sendActivationEmail(registerDTO.getEmail(), request);
        redirectAttributes.addFlashAttribute("successMessage",
            "We've sent an email with the link to active user.");
      } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Please try again.");
      }
      return "redirect:" + AppRoutes.LOGIN;

    } catch (UserAlreadyExistsException e) {
      if (e.getMessage().contains("Email")) {
        result.rejectValue("email", "duplicate", e.getMessage());
      }
      if (e.getMessage().contains("Username")) {
        result.rejectValue("username", "duplicate", e.getMessage());
      }
      return "pages/auth/auth-register";

    } catch (Exception e) {
      model.addAttribute("errorMessage", e);

      return "pages/auth/auth-register";
    }
  }

  @PostMapping(AppRoutes.RESEND_ACTIVATION)
  public String resendActivation(@RequestParam("email") String email,
      RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
    try {
      Optional<User> user = Optional.ofNullable(userService.findByEmail(email));
      if (user.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Email address not found.");
        return "redirect:" + AppRoutes.ACTIVATE_ACCOUNT;
      }

      if (user.get().isEnabled()) {
        redirectAttributes.addFlashAttribute("errorMessage", "This account is already activated.");
        return "redirect:" + AppRoutes.ACTIVATE_ACCOUNT;
      }

      userService.sendActivationEmail(email, request);
      redirectAttributes.addFlashAttribute("successMessage",
          "A new activation email has been sent to your email address.");

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Failed to resend activation email. Please try again.");
    }
    return "redirect:" + AppRoutes.ACTIVATE_ACCOUNT;
  }

  @GetMapping(AppRoutes.ACTIVATE_ACCOUNT)
  public String activateAccount(@RequestParam(value = "token", required = false) String tokenValue, Model model) {
    model.addAttribute("pageTitle", "Activate Account");
    if (tokenValue == null) {
      return "pages/auth/auth-activate-account";
    }

    try {
      userService.activateAccount(tokenValue);
      model.addAttribute("successMessage", "Account is activated successfully");
      return "pages/auth/auth-activate-account";
    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "pages/auth/auth-activate-account";
    }
  }

}
