package com.pjb.kindergarten_suggestion.services;

import java.text.Normalizer;
import java.util.Optional;
import java.util.regex.Pattern;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.exception.UserAlreadyExistsException;
import com.pjb.kindergarten_suggestion.dto.RegisterDTO;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.User;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of((User) authentication.getPrincipal());
    }

    public void validateNewRegistration(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists. Please try another email.");
        }
    }

    @Override
    public User RegisterDTOtoUser(RegisterDTO registerDTO) {
        validateNewRegistration(registerDTO.getEmail());
        User user = new User();
        user.setFullname(registerDTO.getFullName());
        user.setUsername(generateUsername(registerDTO.getFullName()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setPassword(registerDTO.getPassword());
        user.setRole(Role.PARENT);
        user.setActive(false);
        return user;
    }

    private String generateUsername(String fullName) {
        fullName = removeDiacritics(fullName);
        String[] baseName = fullName.trim().toLowerCase().split("\\s+");
        ;

        String firstName = baseName[baseName.length - 1].substring(0, 1).toUpperCase() +
                baseName[baseName.length - 1].substring(1);
        StringBuilder initials = new StringBuilder(firstName);

        for (int i = 0; i < baseName.length - 1; i++) {
            initials.append(baseName[i].substring(0, 1).toUpperCase());
        }

        String usernamePrefix = initials.toString();
        Long userCount = userRepository.getUserCountByUsernamePrefix(usernamePrefix);

        Long incrementalNumber = userCount + 1;
        return usernamePrefix + incrementalNumber;
    }

    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    @Override
    public void handleSaveUsers(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void checkAndUpdateCurrentUser(User user) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof AnonymousAuthenticationToken) {
            return;
        }
        User currentUser = (User) currentAuth.getPrincipal();
        if (currentUser.getId().equals(user.getId())) {
            Authentication newAuth = new UsernamePasswordAuthenticationToken(user, currentAuth.getCredentials(),
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
