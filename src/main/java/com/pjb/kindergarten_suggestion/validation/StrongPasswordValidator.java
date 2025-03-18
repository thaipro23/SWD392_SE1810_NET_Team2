package com.pjb.kindergarten_suggestion.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // check if string contains at least one digit, one lowercase letter, one
        // uppercase letter, one special character and characters long
        return value.matches("^(?=.*\\d)(?=.*[a-zA-Z]).{7,}$");
    }
}