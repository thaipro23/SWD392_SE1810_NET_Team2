package com.pjb.kindergarten_suggestion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.entities.Address;
import com.pjb.kindergarten_suggestion.validation.Email;
import com.pjb.kindergarten_suggestion.validation.PhoneNumber;
import com.pjb.kindergarten_suggestion.validation.RegisterChecked;
import com.pjb.kindergarten_suggestion.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@RegisterChecked
@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "Password is required")
    @StrongPassword
    private String password;
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    private LocalDate birthDate;

    @PhoneNumber
    private String phone;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private Address address;

    private Role role;

    private boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
