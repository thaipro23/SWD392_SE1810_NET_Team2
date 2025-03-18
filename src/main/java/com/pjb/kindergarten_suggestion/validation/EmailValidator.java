package com.pjb.kindergarten_suggestion.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email is required")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() > 255) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email must not exceed 255 characters")
                    .addConstraintViolation();
            return false;
        }

        if (!value.matches(EMAIL_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Please enter a valid email")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}