package com.pjb.kindergarten_suggestion.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private static final String PHONE_PATTERN = "^\\+\\d{1,3}\\d{9,15}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Phone number is required")
                    .addConstraintViolation();
            return false;
        }

        if (!value.matches(PHONE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Phone number is invalid")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}