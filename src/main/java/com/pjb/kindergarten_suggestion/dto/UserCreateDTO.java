package com.pjb.kindergarten_suggestion.dto;

import com.pjb.kindergarten_suggestion.validation.*;
import com.pjb.kindergarten_suggestion.validation.Email;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "Username be")
    private String username;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email
    private String email;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @PhoneNumber
    private String phone;

    @NotBlank(message = "Role is required")
    private String role;

    private boolean active = true;
    private LocalDateTime updatedAt;
}