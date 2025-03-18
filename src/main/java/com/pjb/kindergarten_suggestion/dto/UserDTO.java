package com.pjb.kindergarten_suggestion.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.entities.Address;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username be")
    private String username; 

    private String password;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")   
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "Phone number is required")
    //@Pattern(regexp = "(03|05|07|08|09|01[2689])+([0-9]{8})\\b", message = "Phone number is invalid")
    @Pattern(regexp = "^\\+\\d{1,3}\\d{9,15}$", message = "Phone number is invalid")
    private String phone;

    @NotBlank(message = "Full name is required")
    private String fullname;

    private Address address;

    private Role role;

    private boolean isActive;
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
