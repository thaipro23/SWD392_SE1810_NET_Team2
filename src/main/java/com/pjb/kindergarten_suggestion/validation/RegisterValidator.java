package com.pjb.kindergarten_suggestion.validation;

import com.pjb.kindergarten_suggestion.dto.RegisterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegisterValidator implements ConstraintValidator<RegisterChecked, RegisterDTO> {
    @Override
    public boolean isValid(RegisterDTO user, ConstraintValidatorContext context) {
        boolean valid = true;
        // Check if password fields match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            context.buildConstraintViolationWithTemplate("Passwords must match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        // Additional validations can be added here

        return valid;
    }
}
