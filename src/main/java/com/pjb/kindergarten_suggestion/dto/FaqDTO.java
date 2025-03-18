package com.pjb.kindergarten_suggestion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FaqDTO {

    private Long id;
    @NotBlank(message = "Question is required.")
    private String question;
    @NotBlank(message = "Answer is required.")
    private String answer;
    private boolean status;
}
