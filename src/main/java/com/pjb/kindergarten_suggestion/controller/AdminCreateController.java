
package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.UserCreateDTO;
import com.pjb.kindergarten_suggestion.entities.*;
import com.pjb.kindergarten_suggestion.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminCreateController {
    private final UserService userService;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
    private static final int PASSWORD_LENGTH = 8;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(AppRoutes.ADMIN_USER_CREATE)
    public String showCreateUserForm(Model model) {
        model.addAttribute("pageTitle", "Create User");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserCreateDTO());
        }

        List<String> roles = Arrays.stream(Role.values())
                .map(Role::getName)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);
        return "pages/admin/users/create";
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    @GetMapping("/create/count-username-prefix")
    public ResponseEntity<Long> countUsernamePrefix(@RequestParam String prefix) {
        Long userCount = userService.getUserCountByUsernamePrefix(prefix);
        return ResponseEntity.ok(userCount);
    }
    @PostMapping(AppRoutes.ADMIN_USER_CREATE)
    public String createUser(
            @Valid @ModelAttribute("user") UserCreateDTO userDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            List<String> roles = Arrays.stream(Role.values())
                    .map(Role::getName)
                    .collect(Collectors.toList());
            model.addAttribute("roles", roles);
            return "pages/admin/users/create";
        }

        if (userDTO.getDob() != null && userDTO.getDob().isAfter(LocalDate.now())) {
            result.rejectValue("dob", "error.dob", "Date of birth must be in the past");
        }
        if (userDTO.getPhone() == null) {
            result.rejectValue("phone", "error.phone", "Phone number is required");
        }
        if (userService.isExistPhone(userDTO.getPhone())) {
            result.rejectValue("phone", "error.phone", "Phone number already exist!");
        }
        if (!isValidEmail(userDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Please enter a valid email address");
        }
        if (userService.isExistEmail(userDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Email already exist!");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", userDTO);
            return "redirect:"+AppRoutes.ADMIN_USER_CREATE;
        }

        try {
            String name= removeDiacritics(userDTO.getFullName());
            User user = new User();
            user.setFullname(formatFullName(userDTO.getFullName()));
            user.setEmail(userDTO.getEmail());
            user.setBirthDate(userDTO.getDob());
            user.setPhone(userDTO.getPhone());
            user.setRole(Role.valueOf(userDTO.getRole()));
            user.setActive(userDTO.isActive());
            user.setCreatedAt(LocalDateTime.now());

            String generatedPassword = generateSecurePassword();
            user.setPassword(passwordEncoder.encode(generatedPassword));
            user.setUsername(generateUsername(name));

            User savedUser = userService.createUser(user);
            userService.sendMailCreateUser(savedUser.getEmail(), savedUser.getUsername(), generatedPassword);

            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:"+AppRoutes.ADMIN_USER_CREATE;

        } catch (Exception e) {
            List<String> roles = Arrays.stream(Role.values())
                    .map(Role::getName)
                    .collect(Collectors.toList());
            model.addAttribute("roles", roles);
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "pages/admin/users/create";
        }

    }
    private String formatFullName(String fullName) {
        String[] words = fullName.trim().toLowerCase().split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formattedName.toString().trim();
    }
    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
    private String generateUsername(String fullName) {
        String[] baseName = fullName.trim().toLowerCase().split("\\s+");;

        String firstName = baseName[baseName.length - 1].substring(0, 1).toUpperCase() +
                baseName[baseName.length - 1].substring(1);
        StringBuilder initials = new StringBuilder(firstName);

        for (int i = 0; i < baseName.length - 1; i++) {
            initials.append(baseName[i].substring(0, 1).toUpperCase());
        }

        String usernamePrefix = initials.toString();
        Long userCount = userService.getUserCountByUsernamePrefix(usernamePrefix);

        Long incrementalNumber = userCount + 1;
        return usernamePrefix + incrementalNumber;
    }


    private SecureRandom random = new SecureRandom();

    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        password.append(randomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
                .append(randomChar("abcdefghijklmnopqrstuvwxyz"))
                .append(randomChar("abcdefghijklmnopqrstuvwxyz"))
                .append(randomChar("0123456789"))
                .append(randomChar("!@#$%^&*()-_+=<>?"));

        while (password.length() < PASSWORD_LENGTH) {
            password.append(randomChar(CHAR_POOL));
        }

        return shuffleString(password.toString());
    }

    private char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
    }

    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
