package com.pjb.kindergarten_suggestion.dto;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.entities.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ParentProfileDTO {
    private String username;
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Number phone is required")
    @Pattern(regexp = "^\\+\\d{1,3}\\d{9,15}$", message = "Phone number must start with a + followed by country code and phone number (9-15 digits)")
    private String phone;

    @NotBlank(message = "Full name is required")
    @Pattern(regexp = "^(?=.*[\\p{L}])[\\p{L} ']{2,}$", message = "Full name must be at least 2 characters long and only letters, spaces, and apostrophes")
    private String fullName;

    private Address address = new Address();

    private Role role;

    private boolean isActive;

    private LocalDateTime updatedAt;
}
