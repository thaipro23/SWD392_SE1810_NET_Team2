package com.pjb.kindergarten_suggestion.dto;

import java.time.LocalDateTime;

import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CounsellingRequestDTO {

    private Long id;

    private School school;

    private User parent;
    
    @NotBlank(message = "Full name is required")
    private String fullname;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")  
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+\\d{1,3}\\d{9,15}$", message = "Phone number is invalid")
    private String phone;

    @NotBlank(message = "Inquiry is required")
    private String inquiry;


    private RequestStatus status;

    private LocalDateTime createdAt;

}
